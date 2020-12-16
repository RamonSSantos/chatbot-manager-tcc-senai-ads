<template>
  <div id="login-background">
    <v-card width="400" class="rounded-lg mx-auto px-3 mt-15" elevation="5">
      <v-card-title>
        <h1 class="display-1 mx-auto">
          <span class="font-weight-medium indigo--text text--darken-4">
            chatbot
          </span>
          <span class="font-weight-light indigo--text text--lighten-1">
            Manager
          </span>
        </h1>
      </v-card-title>
      <v-card-text class="pb-0">
        <v-form ref="form" v-model="loginValidation" lazy-validation>
          <v-text-field
            ref="email"
            type="email"
            v-model="form.email"
            label="E-mail"
            :rules="[rules.required, rules.email]"
            prepend-inner-icon="mdi-account-circle"
            @keydown.enter.stop="login"
            outlined
            rounded
            required
          />
          <v-text-field
            :type="showPassword ? 'text' : 'password'"
            v-model="form.password"
            label="Senha"
            :rules="[rules.required, rules.minLength]"
            prepend-inner-icon="mdi-lock"
            :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append="showPassword = !showPassword"
            @keydown.enter.stop="login"
            autocomplete="new-password"
            class="mt-1"
            outlined
            rounded
            required
          />
        </v-form>
      </v-card-text>
      <v-card-actions class="px-3">
        <v-btn
          color="primary"
          :disabled="!loginValidation"
          :loading="loading"
          @click.stop.prevent="login"
          block
          rounded
          raised
        >
          Entrar
        </v-btn>
      </v-card-actions>
      <div class="pa-5">
        <v-divider></v-divider>
        <p class="headline font-weight-light mt-4 mb-2">Esqueceu sua senha?</p>
        <router-link
          :to="{ name: 'ResetPassword' }"
          class="text-decoration-none blue--text text--darken-3"
        >
          Sem problemas! Clique aqui para redefinir.
        </router-link>
      </div>
    </v-card>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Login',
  data: () => ({
    form: {
      email: '',
      password: '',
    },
    showPassword: false,
    loginValidation: true,
    loader: null,
    loading: false,
    rules: {
      required: (v) => !!v,
      minLength: (v) => v.length >= 4,
      email: (v) => {
        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return pattern.test(v)
      },
    },
  }),
  mounted() {
    this.$refs.email.focus()
  },
  methods: {
    ...mapActions(['LogIn']),
    async login() {
      if (!this.$refs.form.validate()) {
        this.loginValidation = true
      } else {
        this.loginValidation = !this.loginValidation
        this.loader = 'loading'

        try {
          await this.LogIn(this.form)
          setTimeout(() => this.$router.push({ name: 'Dashboard' }), 2000)
        } catch {
          setTimeout(() => (this.loginValidation = true), 2000)
        }
      }
    },
  },
  watch: {
    loader(newValue) {
      const l = newValue
      this[l] = !this[l]

      setTimeout(() => (this[l] = false), 2000)

      this.loader = null
    },
  },
}
</script>

<style scoped>
#login-background {
  background-image: url('../../../assets/background.png');
  width: 100%;
  height: 100%;
}
</style>
