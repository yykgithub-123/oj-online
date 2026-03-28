package com.yyk.codesandbox;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate {

    private static final long TIME_OUT = 5000L;

    private static final Boolean FIRST_INIT = true;

    public static void main(String[] args) {
        JavaDockerCodeSandbox javaNativeCodeSandbox = new JavaDockerCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    /**
     * 创建容器，把文件复制到容器内
     * @param userCodeFile
     * @param inputList
     * @return
     */
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
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像：" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
        }

        System.out.println("下载完成");

        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        // 资源限制：内存256MB、禁用Swap、CPU 1核
        hostConfig.withMemory(256 * 1024 * 1024L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        // 挂载代码目录
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                // 安全：禁用网络
                .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();

        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();

        // docker exec keen_blackwell java -cp /app Main 1 3
        // 执行命令并获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("创建执行命令：" + execCreateCmdResponse);

            ExecuteMessage executeMessage = new ExecuteMessage();
            final StringBuilder messageBuilder = new StringBuilder();
            final StringBuilder errorMessageBuilder = new StringBuilder();
            final long[] maxMemory = {0L};
            long time = 0L;
            // 判断是否超时
            final boolean[] timeout = {true};
            String execId = execCreateCmdResponse.getId();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    // 如果执行完成，则表示没超时
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
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }

            // 处理超时逻辑
            if (timeout[0]) {
                System.out.println("程序执行超时");
                executeMessage.setExitValue(-1);
                executeMessage.setErrorMessage("程序执行超时，超过 " + TIME_OUT + " ms");
                executeMessage.setTime(TIME_OUT);
                executeMessageList.add(executeMessage);
                // 超时后跳过内存统计，继续处理下一个输入
                continue;
            }

            // 程序执行后再获取内存统计
            final Statistics[] statsHolder = new Statistics[1];
            final CountDownLatch statsLatch = new CountDownLatch(1);
            dockerClient.statsCmd(containerId)
                    .withNoStream(true)
                    .exec(new ResultCallback<Statistics>() {
                        @Override
                        public void onNext(Statistics statistics) {
                            statsHolder[0] = statistics;
                            statsLatch.countDown();
                        }

                        @Override
                        public void close() throws IOException {
                        }

                        @Override
                        public void onStart(Closeable closeable) {
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            statsLatch.countDown();
                        }

                        @Override
                        public void onComplete() {
                            statsLatch.countDown();
                        }
                    });

            try {
                statsLatch.await(5, TimeUnit.SECONDS);
                if (statsHolder[0] != null && statsHolder[0].getMemoryStats() != null) {
                    maxMemory[0] = statsHolder[0].getMemoryStats().getUsage();
                    System.out.println("内存占用：" + maxMemory[0]);
                }
            } catch (InterruptedException e) {
                System.out.println("获取内存统计超时");
            }

            executeMessage.setMessage(messageBuilder.toString().trim());
            executeMessage.setErrorMessage(errorMessageBuilder.toString().trim());
            executeMessage.setTime(time);
            // 输出完整结果
            if (executeMessage.getMessage() != null && !executeMessage.getMessage().isEmpty()) {
                System.out.println("输出结果：" + executeMessage.getMessage());
            }
            if (executeMessage.getErrorMessage() != null && !executeMessage.getErrorMessage().isEmpty()) {
                System.out.println("输出错误：" + executeMessage.getErrorMessage());
            }
            // 内存单位转换为 MB
            executeMessage.setMemory(maxMemory[0] / 1024 / 1024);
            executeMessageList.add(executeMessage);
        }
        return executeMessageList;
    }
}



