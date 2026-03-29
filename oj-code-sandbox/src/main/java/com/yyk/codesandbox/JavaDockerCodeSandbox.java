package com.yyk.codesandbox;

import cn.hutool.core.io.resource.ResourceUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.yyk.codesandbox.model.ExecuteCodeRequest;
import com.yyk.codesandbox.model.ExecuteCodeResponse;
import com.yyk.codesandbox.model.ExecuteMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Docker 代码沙箱实现
 * 使用文件法传递输入数据（base64 编码 + stdin 重定向）
 *
 * 实现原理：
 * 1. 将输入数据 Base64 编码（避免 shell 转义问题）
 * 2. 通过 exec 命令写入容器内临时文件 /tmp/input.txt
 * 3. 运行 Java 程序时重定向 stdin 到文件
 *
 * 优点：稳定、可靠、易调试
 */
@Component
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;

    private static final Boolean FIRST_INIT = true;

    public static void main(String[] args) {
        JavaDockerCodeSandbox sandbox = new JavaDockerCodeSandbox();
        ExecuteCodeRequest request = new ExecuteCodeRequest();
        // 测试多行输入
        request.setInputList(Arrays.asList("1 2\n", "3 4\n"));
        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        request.setCode(code);
        request.setLanguage("java");
        ExecuteCodeResponse response = sandbox.executeCode(request);
        System.out.println(response);
    }

    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        // 获取 Docker Client（Windows 需要指定 TCP 地址）
        String dockerHost = System.getProperty("os.name").toLowerCase().contains("win")
                ? "tcp://localhost:2375"
                : null;
        DockerClient dockerClient = dockerHost != null
                ? DockerClientBuilder.getInstance(dockerHost).build()
                : DockerClientBuilder.getInstance().build();

        // 拉取镜像
        String image = "eclipse-temurin:17-jdk-alpine";
        if (FIRST_INIT) {
            pullImage(dockerClient, image);
        }

        System.out.println("镜像准备完成");

        // 创建容器
        String containerId = createContainer(dockerClient, image, userCodeParentPath);

        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();

        // 执行代码并收集结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            ExecuteMessage message = executeWithInput(dockerClient, containerId, inputArgs);
            executeMessageList.add(message);
        }

        return executeMessageList;
    }

    /**
     * 拉取 Docker 镜像
     */
    private void pullImage(DockerClient dockerClient, String image) {
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
        PullImageResultCallback callback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println("下载镜像：" + item.getStatus());
                super.onNext(item);
            }
        };
        try {
            pullImageCmd.exec(callback).awaitCompletion();
        } catch (InterruptedException e) {
            System.out.println("拉取镜像异常");
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建 Docker 容器
     */
    private String createContainer(DockerClient dockerClient, String image, String userCodeParentPath) {
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(256 * 1024 * 1024L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));

        CreateContainerResponse response = dockerClient.createContainerCmd(image)
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();

        System.out.println("创建容器：" + response);
        return response.getId();
    }

    /**
     * 使用文件法执行代码
     * 1. Base64 编码输入数据
     * 2. 写入容器内临时文件
     * 3. 运行程序，stdin 重定向到文件
     */
    private ExecuteMessage executeWithInput(DockerClient dockerClient, String containerId, String inputArgs) {
        StopWatch stopWatch = new StopWatch();
        ExecuteMessage executeMessage = new ExecuteMessage();

        // ========== 步骤 1: 将输入数据写入容器内临时文件 ==========
        // 使用 Base64 编码避免 shell 转义和换行符问题
        String base64Input = Base64.getEncoder().encodeToString(
                inputArgs.getBytes(StandardCharsets.UTF_8)
        );

        // 命令：将 base64 解码后的数据写入 /tmp/input.txt
        String writeCmd = "echo " + base64Input + " | base64 -d > /tmp/input.txt";

        try {
            ExecCreateCmdResponse writeExec = dockerClient.execCreateCmd(containerId)
                    .withCmd("sh", "-c", writeCmd)
                    .exec();

            dockerClient.execStartCmd(writeExec.getId())
                    .exec(new ExecStartResultCallback())
                    .awaitCompletion(5, TimeUnit.SECONDS);

            System.out.println("输入数据已写入 /tmp/input.txt");
        } catch (Exception e) {
            System.out.println("写入输入文件失败：" + e.getMessage());
            executeMessage.setErrorMessage("写入输入文件失败：" + e.getMessage());
            return executeMessage;
        }

        // ========== 步骤 2: 运行 Java 程序，stdin 重定向到文件 ==========
        final StringBuilder messageBuilder = new StringBuilder();
        final StringBuilder errorMessageBuilder = new StringBuilder();
        final boolean[] timeout = {true};

        // 命令：java -cp /app Main < /tmp/input.txt
        String runCmd = "java -cp /app Main < /tmp/input.txt";

        ExecCreateCmdResponse runExec = dockerClient.execCreateCmd(containerId)
                .withCmd("sh", "-c", runCmd)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();

        System.out.println("执行命令：" + runCmd);

        ExecStartResultCallback callback = new ExecStartResultCallback() {
            @Override
            public void onComplete() {
                timeout[0] = false;
                super.onComplete();
            }

            @Override
            public void onNext(Frame frame) {
                StreamType streamType = frame.getStreamType();
                String payload = new String(frame.getPayload());
                if (StreamType.STDERR.equals(streamType)) {
                    errorMessageBuilder.append(payload);
                } else {
                    messageBuilder.append(payload);
                }
                super.onNext(frame);
            }
        };

        try {
            stopWatch.start();
            dockerClient.execStartCmd(runExec.getId())
                    .exec(callback)
                    .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
            stopWatch.stop();
        } catch (InterruptedException e) {
            System.out.println("程序执行异常");
            throw new RuntimeException(e);
        }

        // ========== 处理超时 ==========
        if (timeout[0]) {
            System.out.println("程序执行超时");
            executeMessage.setExitValue(-1);
            executeMessage.setErrorMessage("程序执行超时，超过 " + TIME_OUT + " ms");
            executeMessage.setTime(TIME_OUT);
            return executeMessage;
        }

        // ========== 获取内存统计 ==========
        long maxMemory = getContainerMemory(dockerClient, containerId);

        // ========== 封装结果 ==========
        executeMessage.setMessage(messageBuilder.toString().trim());
        executeMessage.setErrorMessage(errorMessageBuilder.toString().trim());
        executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        executeMessage.setMemory(maxMemory / 1024 / 1024);

        // 输出结果
        if (executeMessage.getMessage() != null && !executeMessage.getMessage().isEmpty()) {
            System.out.println("输出结果：" + executeMessage.getMessage());
        }
        if (executeMessage.getErrorMessage() != null && !executeMessage.getErrorMessage().isEmpty()) {
            System.out.println("输出错误：" + executeMessage.getErrorMessage());
        }

        return executeMessage;
    }

    /**
     * 获取容器内存使用量
     */
    private long getContainerMemory(DockerClient dockerClient, String containerId) {
        final long[] maxMemory = {0L};
        final CountDownLatch latch = new CountDownLatch(1);

        dockerClient.statsCmd(containerId)
                .withNoStream(true)
                .exec(new ResultCallback<Statistics>() {
                    @Override
                    public void onNext(Statistics statistics) {
                        if (statistics != null && statistics.getMemoryStats() != null) {
                            maxMemory[0] = statistics.getMemoryStats().getUsage();
                            System.out.println("内存占用：" + maxMemory[0]);
                        }
                        latch.countDown();
                    }

                    @Override
                    public void close() throws IOException {}
                    @Override
                    public void onStart(Closeable closeable) {}
                    @Override
                    public void onError(Throwable throwable) {
                        latch.countDown();
                    }
                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("获取内存统计超时");
        }

        return maxMemory[0];
    }
}