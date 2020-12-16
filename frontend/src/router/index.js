import Vue from 'vue'
import Router from 'vue-router'
import NProgress from 'nprogress'
import store from '@/store'
import routes from './routes'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  base: '/web',
  routes: routes,
})

/*router.beforeResolve(async (to, from, next) => {
  if (to.name !== 'Login') {
    NProgress.configure({ parent: '#main', ease: 'ease', speed: 500 }).start()
  }

  next()
})*/

router.beforeEach(async (to, from, next) => {
  document.title = `${to.meta.title} - Chatbot | Área Administrativa`

  if (to.name !== 'Login' || to.name !== 'ResetPassword') {
    NProgress.start()
  }

  if (to.name === 'ResetPassword') {
    next()
    return
  }

  if (to.name !== 'Login' && !store.getters['isAuthenticated']) {
    try {
      await store.dispatch('CheckToken')
      next({ path: to.path })
    } catch {
      next({ name: 'Login' })
    }
  } else if (to.name === 'Login' && store.getters['isAuthenticated']) {
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

router.afterEach(() => NProgress.done())

export default router
