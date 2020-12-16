import Vue from 'vue'
import vuetify from '@/plugins/vuetify'
import VuetifyMask from '@/plugins/vuetify-mask'
import store from '@/store'
import router from './router'
import Filters from '@/utils/filters'
import App from './App.vue'

import 'nprogress/nprogress.css'

Vue.config.productionTip = false

window.bus = new Vue()

Vue.use(Filters)

new Vue({
  store,
  router,
  vuetify,
  VuetifyMask,
  render: (h) => h(App),
}).$mount('#app')
