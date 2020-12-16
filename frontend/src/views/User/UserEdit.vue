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
        <span class="headline">Editar Usuário</span>
      </v-card-title>
      <v-card-text class="pb-0">
        <v-container class="pb-0">
          <v-row>
            <v-col cols="12" sm="6" md="6">
              <v-text-field-cpf
                v-model="editObject.cpf"
                v-bind:properties="{
                  disabled: !disabled,
                  outlined: true,
                }"
                v-bind:options="{
                  outputMask: '###########',
                  empty: null,
                }"
                label="CPF"
              >
              </v-text-field-cpf>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-text-field
                v-model="editObject.registration"
                label="Matrícula"
                :disabled="!disabled"
                outlined
              >
              </v-text-field>
            </v-col>
          </v-row>
          <v-form ref="editUser" v-model="valid" lazy-validation>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  ref="fullnameElement"
                  v-model="editObject.fullname"
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
                  v-model="editObject.email"
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
                  v-model="editObject.cellphone"
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
                  v-model="editObject.profile"
                  :items="profiles"
                  item-text="description"
                  :rules="[rules.required]"
                  label="Perfil*"
                  :disabled="disabled"
                  no-data-text="Selecione algum perfil"
                  return-object
                  clearable
                  outlined
                  required
                >
                </v-autocomplete>
              </v-col>
              <v-col cols="12" sm="6" md="6">
                <v-autocomplete
                  v-model="editObject.sector"
                  :items="sectors"
                  item-text="description"
                  :rules="[rules.required]"
                  label="Setor*"
                  :disabled="disabled"
                  no-data-text="Selecione algum setor"
                  return-object
                  clearable
                  outlined
                  required
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
        <v-btn @click.stop="resetForm" color="warning darken-1" text>
          Limpar
        </v-btn>
        <v-btn
          :disabled="!valid"
          @click.stop="editUser"
          color="blue darken-1"
          text
        >
          Editar
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

export default {
  name: 'UserEdit',
  props: {
    object: {
      type: Object,
      default: null,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    getChangesOnEditUser() {
      return this.object.fullname === this.editObject.fullname &&
        this.object.email === this.editObject.email &&
        this.object.cellphone === this.editObject.cellphone &&
        this.object.profile === this.editObject.profile &&
        this.object.sector === this.editObject.sector
        ? true
        : false
    },
  },
  data: () => ({
    dialog: false,
    valid: true,
    editObject: {
      id: null,
      cpf: null,
      registration: null,
      fullname: null,
      email: null,
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
      cellphoneLength: (v) =>
        (v && v.length == 15) ||
        'O telefone deve ser no formato (00) 00000-0000',
    },
  }),
  created() {
    this.dialog = true
    this.editObject = Object.assign({}, this.object)
    this.getProfiles()
    this.getSectors()
  },
  mounted() {
    this.$refs.fullnameElement.focus()
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
    async editUser() {
      if (this.getChangesOnEditUser) {
        const notification = {
          type: 'yellow darken-2',
          message: 'Altere algum campo para realizar a alteração!',
          icon: 'mdi-alert-circle',
          isLight: true,
        }
        this.$store.dispatch('notification/add', notification, { root: true })
      } else if (this.$refs.editUser.validate()) {
        await UserService.edit(this.editObject)
        this.closeDialog(true)
      }
    },
    resetForm() {
      this.$refs.editUser.reset()
    },
    closeDialog(updateTable) {
      EventBus.$emit('users', updateTable)
      this.dialog = false
      this.$router.push({ name: 'Users' })
    },
  },
}
</script>
