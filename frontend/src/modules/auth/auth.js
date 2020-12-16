import services from './services'
import * as storage from './storage'

const state = {
  user: null,
}

const getters = {
  isAuthenticated: ({ user }) => !!user,
  stateUser: ({ user }) => user,
}

const actions = {
  async LogIn({ dispatch }, user) {
    const response = await services.authenticate(user)
    dispatch('ActionSetUser', JSON.stringify(response.data))
  },

  ActionSetUser({ commit }, payload) {
    storage.setLocalUser(payload)
    commit('setUser', payload)
  },

  CheckToken({ dispatch, state }) {
    const user = storage.getLocalUser()
    if (!user) {
      return Promise.reject(new Error('Autenticação inválida'))
    }
    dispatch('ActionSetUser', state.user)
    return dispatch('LoadSession')
  },

  async LoadSession({ dispatch }) {
    try {
      const response = await services.validateToken()
      dispatch('ActionSetUser', JSON.stringify(response.data))
    } catch (err) {
      dispatch('DeleteLocalUser')
    }
  },

  DeleteLocalUser({ commit }) {
    storage.deleteLocalUser()

    let user = null
    commit('setUser', user)
  },

  async LogOut({ commit }) {
    await services.logout()
    storage.deleteLocalUser()

    let user = null
    commit('setUser', user)
  },
}

const mutations = {
  setUser(state, user) {
    state.user = user
  },
}

export default {
  state,
  getters,
  actions,
  mutations,
}
