// initial state
import { StoreOptions } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "../../generated";

export default {
  namespaced: true,
  state: () => ({
    loginUser: {
      userName: "未登录",
    },
  }),
  actions: {
    async getLoginUser({ commit, state }, payload) {
      try {
        // 从远程请求获取登录信息
        const res = await UserControllerService.getLoginUserUsingGet();
        if (res.code === 0 && res.data) {
          console.log('LoginUserres.data', res.data);
          
          commit("updateUser", res.data);
        } else {
          commit("updateUser", {
            userName: "未登录",
            userRole: ACCESS_ENUM.NOT_LOGIN,
          });
        }
      } catch (error) {
        // 如果API返回错误（比如未登录），设置默认状态
        commit("updateUser", {
          userName: "未登录",
          userRole: ACCESS_ENUM.NOT_LOGIN,
        });
      }
    },
    async logout({ commit }){
      const res = await UserControllerService.userLogoutUsingPost();
      if (res.code === 0) {
      }
    }
  },
  mutations: {
    updateUser(state, payload) {
      state.loginUser = payload;
    },
  },
} as StoreOptions<any>;
