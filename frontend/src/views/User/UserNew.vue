<template>
  <v-dialog
    v-model="dialog"
    max-width="600px"
    @click:outside="closeDialog(false)"
    @keydown.esc="closeDialog(false)"
    transition="slide-x-reverse-transition"
    scrollable
  >
    <v-card>
      <v-card-title>
        <span class="headline">Novo Usuário</span>
      </v-card-title>
      <v-card-text class="pb-0">
        <v-container class="pb-0">
          <v-form ref="newUser" v-model="valid" lazy-validatio>
            <v-row>
              <v-col cols="12" sm="6" md="6">
                <v-text-field-cpf
                  ref="cpfElement"
                  v-model="object.cpf"
                  v-bind:properties="{
                    rules: [rules.required, rules.cpfLength, rules.isValidCpf],
                    counter: 14,
                    disabled: disabled,
                    outlined: true,
                  }"
                  v-bind:options="{
                    outputMask: '###########',
                    empty: null,
                  }"
                  label="CPF*"
                  required
                >
                </v-text-field-cpf>
              </v-col>
              <v-col cols="12" sm="6" md="6">
                <v-text-field
                  v-model="object.registration"
                  :rules="[rules.required, rules.registrationLength]"
                  :counter="8"
                  label="Matrícula*"
                  :disabled="disabled"
                  outlined
                  required
                >
                </v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="object.fullname"
                  :rules="[rules.required, rules.fullnameLength]"
                  label="Nome completo*"
                  :disabled="disabled"
                  outlined
                  required
                >
                </v-text-field>
              </v-col>
              <v-col cols="12" sm="6" md="6">
                <v-text-field
                  v-model="object.email"
                  :rules="[rules.required, rules.email]"
                  label="E-mail*"
                  :disabled="disabled"
                  outlined
                  required
                >
                </v-text-field>
              </v-col>
              <v-col cols="12" sm="6" md="6">
                <v-text-field-simplemask
                  v-model="object.cellphone"
                  v-bind:properties="{
                    rules: [rules.required, rules.cellphoneLength],
                    counter: 15,
                    disabled: disabled,
                    outlined: true,
                  }"
                  v-bind:options="{
                    inputMask: '(##) #####-####',
                    outputMask: '###########',
                    empty: null,
                    applyAfter: false,
                    alphanumeric: true,
                    lowerCase: false,
                  }"
                  label="Telefone*"
                  required
                >
                </v-text-field-simplemask>
              </v-col>
              <v-col cols="12" sm="6" md="6">
                <v-autocomplete
                  v-model="object.profile"
                  :items="profiles"
                  item-text="description"
                  :rules="[rules.required]"
                  label="Perfil*"
                  :disabled="disabled"
                  no-data-text="Selecione algum perfil"
                  return-object
                  clearable
                  outlined
                >
                </v-autocomplete>
              </v-col>
              <v-col cols="12" sm="6" md="6">
                <v-autocomplete
                  v-model="object.sector"
                  :items="sectors"
                  item-text="description"
                  :rules="[rules.required]"
                  label="Setor*"
                  :disabled="disabled"
                  no-data-text="Selecione algum setor"
                  return-object
                  clearable
                  outlined
                >
                </v-autocomplete>
              </v-col>
            </v-row>
          </v-form>
        </v-container>
        <small class="red--text">*Campos obrigatórios!</small>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions class="pt-0">
        <v-btn @click.stop="closeDialog(false)" color="grey darken-1" text>
          Fechar
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn @click="resetForm" color="warning darken-1" text>
          Limpar
        </v-btn>
        <v-btn
          :disabled="!valid"
          @click.stop="newUser"
          color="blue darken-1"
          text
        >
          Criar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import EventBus from '@/services/EventBus'
import ProfileService from '@/services/ProfileService'
import SectorService from '@/services/SectorService'
import UserService from '@/services/UserService'
import validateCpf from '@/utils/validateCpf'

export default {
  name: 'UserEdit',
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    dialog: false,
    valid: true,
    object: {
      cpf: null,
      registration: null,
      email: null,
      fullname: null,
      cellphone: null,
      profile: null,
      sector: null,
    },
    profiles: [],
    sectors: [],
    rules: {
      required: (v) => !!v || 'Preencha este campo!',
      email: (v) => {
        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return pattern.test(v) || 'E-mail inválido'
      },
      fullnameLength: (v) =>
        (v && v.length >= 8 && v.length <= 45) ||
        'O nome deve ter entre 8 a 45 caracteres',
      cpfLength: (v) =>
        (v && v.length == 14) || 'O CPF deve ser no formato 000.000.000-00',
      isValidCpf: (v) => (v && validateCpf(v)) || 'CPF inválido',
      registrationLength: (v) =>
        (v && v.length == 8) || 'A matrícula deve ter 8 números',
      cellphoneLength: (v) =>
        (v && v.length == 15) ||
        'O telefone deve ser no formato (00) 00000-0000',
    },
  }),
  created() {
    this.dialog = true
    this.getProfiles()
    this.getSectors()
  },
  mounted() {
    this.$refs.cpfElement.focus()
  },
  methods: {
    async getProfiles() {
      const response = await ProfileService.getAllActiveProfiles()
      this.profiles = response.data
    },
    async getSectors() {
      const response = await SectorService.getAllActiveSectors()
      this.sectors = response.data
    },
    async newUser() {
      if (this.$refs.newUser.validate()) {
        await UserService.new(this.object)
        this.closeDialog(true)
      }
    },
    resetForm() {
      this.$refs.newUser.reset()
    },
    closeDialog(updateTable) {
      EventBus.$emit('users', updateTable)
      this.dialog = false
      this.$router.push({ name: 'Users' })
    },
  },
}
</script>
