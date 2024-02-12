import { defineStore } from "pinia";

export const usePickStore = defineStore("pick", {
  state: () => ({
    code: "",

    userInfo: [],

    unitInfo: [],

    subscription: {},

    receivedMessage: {},

    publisher: {},

    nowPickPlayerInfo: {
      email: "",
      time: "",
    },

    finished: false,
  }),

  actions: {},

  persist: {
    enabled: true, //storage 저장유무
    strategies: [
      {
        key: "pick",
        storage: localStorage,
      },
    ],
  },
});
