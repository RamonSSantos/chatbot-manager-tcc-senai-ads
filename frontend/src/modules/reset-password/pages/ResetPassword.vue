<template>
  <div id="reset-password">
    <v-card width="400" class="rounded-lg mx-auto px-3 mt-15" elevation="5">
      <v-card-title>
        <h1 class="headline mx-auto">
          Recuperação de senha
        </h1>
      </v-card-title>
      <div v-if="step === 1">
        <v-card-text class="pb-0">
          <v-form
            ref="forgotPasswordForm"
            v-model="forgotPasswordValidation"
            lazy-validation
          >
            <v-text-field-cpf
              ref="cpf"
              v-model="form.cpf"
              v-bind:properties="{
                rules: [rules.required, rules.cpfLength, rules.isValidCpf],
                counter: 14,
                outlined: true,
                rounded: true,
              }"
              v-bind:options="{
                outputMask: '###########',
                empty: null,
              }"
              label="CPF*"
              required
            />
            <v-text-field
              v-model="form.registration"
              :rules="[rules.required, rules.registrationLength]"
              :counter="8"
              label="Matrícula*"
              outlined
              rounded
              required
            />
            <v-text-field
              v-model="form.email"
              :rules="[rules.required, rules.email]"
              label="E-mail*"
              @keydown.enter.stop="forgotPassword"
              outlined
              rounded
              required
            />
          </v-form>
        </v-card-text>
        <v-card-actions class="px-3">
          <v-btn @click="resetForgotPasswordForm" color="warning darken-1" text>
            Limpar
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            :disabled="!forgotPasswordValidation"
            @click.stop.prevent="forgotPassword"
            text
          >
            Confirmar
          </v-btn>
        </v-card-actions>
      </div>
      <div v-else-if="step === 2">
        <v-alert text dense color="teal" icon="mdi-lock-reset" border="left">
          {{ forgotPasswordResponse }}
        </v-alert>
        <v-card-text class="pb-0">
          <v-form
            ref="resetPasswordForm"
            v-model="resetPasswordValidation"
            lazy-validation
          >
            <v-text-field
              ref="password"
              :type="showPassword ? 'text' : 'password'"
              v-model="form.password"
              label="Senha"
              :rules="[
                rules.required,
                rules.passwordContainNumber,
                rules.passwordContainSpecialCharacter,
                rules.passwordLength,
              ]"
              :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append="showPassword = !showPassword"
              autocomplete="new-password"
              counter
              outlined
              rounded
              required
            />
            <v-text-field
              :type="showPasswordConfirmation ? 'text' : 'password'"
              v-model="form.passwordConfirmation"
              label="Confirmar Senha"
              :rules="[
                rules.required,
                rules.passwordContainNumber,
                rules.passwordContainSpecialCharacter,
                rules.passwordLength,
                passwordMatch,
              ]"
              :append-icon="
                showPasswordConfirmation ? 'mdi-eye' : 'mdi-eye-off'
              "
              @click:append="
                showPasswordConfirmation = !showPasswordConfirmation
              "
              @keydown.enter.stop="resetPassword"
              autocomplete="new-password"
              counter
              outlined
              rounded
              required
            />
          </v-form>
        </v-card-text>
        <v-card-actions class="px-3">
          <v-btn @click="resetResetPasswordForm" color="warning darken-1" text>
            Limpar
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            :disabled="!resetPasswordValidation"
            @click.stop.prevent="resetPassword"
            text
          >
            Resetar Senha
          </v-btn>
        </v-card-actions>
      </div>
    </v-card>
  </div>
</template>

<script>
import PasswordService from '../services'
import validateCpf from '@/utils/validateCpf'

export default {
  name: 'ResetPassword',
  computed: {
    passwordMatch: {
      get() {
        return () =>
          (this.form.passwordConfirmation === this.form.password &&
            this.passwordConfirmation) ||
          'As senhas não conferem!'
      },
      set(newVal) {
        if (newVal === this.form.password) {
          this.passwordConfirmation = true
        }
        return newVal
      },
    },
  },
  data: () => ({
    step: 1,
    form: {
      cpf: null,
      registration: null,
      email: null,
      password: null,
      passwordConfirmation: null,
    },
    passwordConfirmation: true,
    showPassword: false,
    showPasswordConfirmation: false,
    forgotPasswordValidation: true,
    resetPasswordValidation: true,
    loader: null,
    forgotPasswordResponse: null,
    rules: {
      required: (v) => !!v,
      cpfLength: (v) =>
        (v && v.length == 14) || 'O CPF deve ser no formato 000.000.000-00',
      isValidCpf: (v) => (v && validateCpf(v)) || 'CPF inválido',
      registrationLength: (v) =>
        (v && v.length == 8) || 'A matrícula deve ter 8 números',
      email: (v) => {
        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return pattern.test(v)
      },
      passwordLength: (v) =>
        (v && v.length >= 8) || 'A senha deve ter pelo menos 8 caracteres',
      passwordContainNumber: (v) => {
        return /\d/.test(v) || 'A senha deve ter pelo menos algum número'
      },
      passwordContainSpecialCharacter: (v) => {
        // eslint-disable-next-line
        const checkSpecialCharacter = /[ !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/
        return (
          checkSpecialCharacter.test(v) ||
          'A senha deve ter pelo menos algum caractere especial'
        )
      },
    },
  }),
  mounted() {
    this.$refs.cpf.focus()
  },
  methods: {
    async forgotPassword() {
      if (!this.$refs.forgotPasswordForm.validate()) {
        this.forgotPasswordValidation = true
      } else {
        this.forgotPasswordValidation = !this.forgotPasswordValidation

        try {
          const response = await PasswordService.forgotPassword(this.form)
          this.forgotPasswordResponse = response.data

          this.step = 2
        } catch {
          setTimeout(() => (this.forgotPasswordValidation = true), 1000)
        }
      }
    },
    async resetPassword() {
      if (!this.$refs.resetPasswordForm.validate()) {
        this.resetPasswordValidation = true
      } else {
        this.resetPasswordValidation = !this.resetPasswordValidation

        try {
          await PasswordService.resetPassword(this.form)
          setTimeout(() => this.$router.push({ name: 'Login' }), 1000)
        } catch {
          setTimeout(() => (this.resetPasswordValidation = true), 1000)
        }
      }
    },
    resetForgotPasswordForm() {
      this.$refs.forgotPasswordForm.reset()
    },
    resetResetPasswordForm() {
      this.$refs.resetPasswordForm.reset()
    },
  },
  watch: {
    step(newValue) {
      if (newValue === 2) {
        setTimeout(() => {
          this.$refs.password.focus()
        }, 1)
      }
    },
  },
}
</script>

<style scoped>
#reset-password {
  background-image: url('../../../assets/background.png');
  width: 100%;
  height: 100%;
}
</style>
