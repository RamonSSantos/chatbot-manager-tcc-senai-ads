<template>
  <v-card class="grey lighten-4 my-4">
    <Breadcrumb />
    <v-card-title class="text-uppercase grey--text text--darken-1 pt-0 pl-6">
      Meu perfil
    </v-card-title>
    <v-card-text class="pb-0">
      <v-container>
        <v-expansion-panels v-model="panel" :readonly="readonly" multiple>
          <v-expansion-panel>
            <v-expansion-panel-header class="text-uppercase">
              Dados Gerais
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <v-skeleton-loader v-if="statusGetUser" type="article, actions">
              </v-skeleton-loader>
              <v-card outlined v-else>
                <v-card-text class="py-0">
                  <v-container>
                    <v-form
                      ref="generalInformationForm"
                      v-model="generalInformation"
                      lazy-validation
                    >
                      <v-row>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field-cpf
                            v-model="user.cpf"
                            v-bind:properties="{
                              disabled: true,
                              filled: true,
                            }"
                            label="CPF"
                            required
                          >
                          </v-text-field-cpf>
                        </v-col>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field
                            v-model="user.registration"
                            label="Matrícula"
                            :disabled="true"
                            filled
                            required
                          >
                          </v-text-field>
                        </v-col>
                        <v-col cols="12">
                          <v-text-field
                            ref="fullnameElement"
                            v-model="user.fullname"
                            :rules="[rules.required, rules.fullnameLength]"
                            label="Nome completo"
                            filled
                            required
                          >
                          </v-text-field>
                        </v-col>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field
                            v-model="user.email"
                            :rules="[rules.required, rules.email]"
                            label="E-mail"
                            filled
                            required
                          >
                          </v-text-field>
                        </v-col>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field-simplemask
                            v-model="user.cellphone"
                            v-bind:properties="{
                              rules: [rules.required, rules.cellphoneLength],
                              counter: 15,
                              filled: true,
                            }"
                            v-bind:options="{
                              inputMask: '(##) #####-####',
                              outputMask: '###########',
                              empty: null,
                              applyAfter: false,
                              alphanumeric: true,
                              lowerCase: false,
                            }"
                            label="Telefone"
                            @keydown.enter.stop="editGeneralInformation"
                            required
                          >
                          </v-text-field-simplemask>
                        </v-col>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field
                            v-model="user.profile.description"
                            label="Perfil"
                            :disabled="true"
                            filled
                            required
                          >
                          </v-text-field>
                        </v-col>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field
                            v-model="user.sector.description"
                            label="Setor"
                            :disabled="true"
                            filled
                            required
                          >
                          </v-text-field>
                        </v-col>
                      </v-row>
                    </v-form>
                  </v-container>
                </v-card-text>
                <v-card-actions class="grey lighten-4 py-3">
                  <v-spacer></v-spacer>
                  <v-btn @click="resetGeneralInformationForm" text
                    >Limpar</v-btn
                  >
                  <v-btn
                    :disabled="!generalInformation"
                    @click.stop="editGeneralInformation"
                    color="blue darken-1"
                    text
                  >
                    Salvar
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-expansion-panel-content>
          </v-expansion-panel>
          <v-expansion-panel>
            <v-expansion-panel-header class="text-uppercase">
              Senha de Acesso
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <v-skeleton-loader v-if="statusGetUser" type="article, actions">
              </v-skeleton-loader>
              <v-card outlined v-else>
                <v-card-text class="py-0">
                  <v-container>
                    <v-form
                      ref="passwordForm"
                      v-model="password"
                      lazy-validation
                    >
                      <v-row>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field
                            :type="showOldPassword ? 'text' : 'password'"
                            v-model="form.oldPassword"
                            label="Senha Antiga"
                            :rules="[rules.required]"
                            :append-icon="
                              showOldPassword ? 'mdi-eye' : 'mdi-eye-off'
                            "
                            @click:append="showOldPassword = !showOldPassword"
                            autocomplete="new-password"
                            counter
                            filled
                            required
                          />
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field
                            :type="showNewPassword ? 'text' : 'password'"
                            v-model="form.newPassword"
                            label="Nova Senha"
                            :rules="[
                              rules.required,
                              rules.passwordContainNumber,
                              rules.passwordContainSpecialCharacter,
                              rules.passwordLength,
                            ]"
                            :append-icon="
                              showNewPassword ? 'mdi-eye' : 'mdi-eye-off'
                            "
                            @click:append="showNewPassword = !showNewPassword"
                            autocomplete="new-password"
                            counter
                            filled
                            required
                          />
                        </v-col>
                        <v-col cols="12" sm="6" md="6">
                          <v-text-field
                            :type="
                              showNewPasswordConfirmation ? 'text' : 'password'
                            "
                            v-model="form.newPasswordConfirmation"
                            label="Confirmar Senha"
                            :rules="[
                              rules.required,
                              rules.passwordContainNumber,
                              rules.passwordContainSpecialCharacter,
                              rules.passwordLength,
                              passwordMatch,
                            ]"
                            :append-icon="
                              showNewPasswordConfirmation
                                ? 'mdi-eye'
                                : 'mdi-eye-off'
                            "
                            @click:append="
                              showNewPasswordConfirmation = !showNewPasswordConfirmation
                            "
                            @keydown.enter.stop="editPassword"
                            autocomplete="new-password"
                            counter
                            filled
                            required
                          />
                        </v-col>
                      </v-row>
                    </v-form>
                  </v-container>
                </v-card-text>
                <v-card-actions class="grey lighten-4 py-3">
                  <v-spacer></v-spacer>
                  <v-btn @click="resetPasswordForm" text>Limpar</v-btn>
                  <v-btn
                    :disabled="!password"
                    @click="editPassword"
                    color="blue darken-1"
                    text
                  >
                    Salvar
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script>
import Breadcrumb from '@/components/breadcrumb/Breadcrumb'
import UserService from '@/services/UserService'

export default {
  name: 'MyProfile',
  components: {
    Breadcrumb,
  },
  computed: {
    passwordMatch: {
      get() {
        return () =>
          (this.form.newPasswordConfirmation === this.form.newPassword &&
            this.passwordConfirmation) ||
          'As senhas não conferem!'
      },
      set(newVal) {
        if (newVal === this.form.newPassword) {
          this.passwordConfirmation = true
        }
        return newVal
      },
    },
  },
  data: () => ({
    panel: [0, 1],
    readonly: true,
    statusGetUser: true,
    user: {
      id: null,
      cpf: null,
      registration: null,
      status: null,
      email: null,
      fullname: null,
      cellphone: null,
      profile: {
        id: null,
        description: null,
      },
      sector: {
        id: null,
        description: null,
      },
    },
    generalInformation: true,
    form: {
      userId: null,
      oldPassword: null,
      newPassword: null,
      newPasswordConfirmation: null,
    },
    password: true,
    showOldPassword: false,
    showNewPassword: false,
    showNewPasswordConfirmation: false,
    passwordConfirmation: true,
    rules: {
      required: (v) => !!v || 'Preencha este campo!',
      email: (v) => {
        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return pattern.test(v) || 'E-mail inválido'
      },
      fullnameLength: (v) =>
        (v && v.length >= 8 && v.length <= 45) ||
        'O nome deve ter entre 8 a 45 caracteres',
      cellphoneLength: (v) =>
        (v && v.length == 15) ||
        'O telefone deve ser no formato (00) 00000-0000',
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
  created() {
    this.getUser()
  },
  methods: {
    async getUser() {
      const stateUser = this.$store.getters['stateUser']
      const userAuthenticated = JSON.parse(stateUser)

      const response = await UserService.getOwnUser(userAuthenticated.userId)
      this.user = response.data
      this.statusGetUser = false
    },
    async editGeneralInformation() {
      if (this.$refs.generalInformationForm.validate()) {
        await UserService.editOwnUser(this.user)
      }
    },
    async editPassword() {
      if (this.$refs.passwordForm.validate()) {
        this.form.userId = this.user.id
        await UserService.editOwnPassword(this.form)
      }
    },
    resetGeneralInformationForm() {
      this.user.fullname = null
      this.user.email = null
      this.user.cellphone = null
    },
    resetPasswordForm() {
      this.$refs.passwordForm.reset()
    },
  },
}
</script>
