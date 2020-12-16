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
        <span class="headline">Visualizar Usuário</span>
      </v-card-title>
      <v-card-text class="pb-0">
        <v-container class="pb-0">
          <v-row>
            <v-col cols="12" sm="6" md="6">
              <v-text-field-cpf
                v-model="object.cpf"
                v-bind:properties="{
                  disabled: disabled,
                  filled: true,
                }"
                label="CPF"
              >
              </v-text-field-cpf>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-text-field
                v-model="object.registration"
                label="Matrícula"
                :disabled="disabled"
                filled
              >
              </v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="object.fullname"
                label="Nome completo"
                :disabled="disabled"
                filled
              >
              </v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-text-field
                v-model="object.email"
                label="E-mail"
                :disabled="disabled"
                filled
              >
              </v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-text-field-simplemask
                v-model="object.cellphone"
                v-bind:properties="{
                  disabled: disabled,
                  filled: true,
                }"
                v-bind:options="{
                  inputMask: '(##) #####-####',
                }"
                label="Telefone"
              >
              </v-text-field-simplemask>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-text-field
                v-model="object.profile.description"
                label="Perfil"
                :disabled="disabled"
                filled
              >
              </v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="6">
              <v-text-field
                v-model="object.sector.description"
                label="Setor"
                :disabled="disabled"
                filled
              >
              </v-text-field>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions class="pt-0">
        <v-spacer></v-spacer>
        <v-btn @click.stop="closeDialog(false)" color="grey darken-1" text>
          Fechar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import EventBus from '@/services/EventBus'

export default {
  name: 'UserView',
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
  data: () => ({
    dialog: false,
  }),
  created() {
    this.dialog = true
  },
  methods: {
    closeDialog(updateTable) {
      EventBus.$emit('users', updateTable)
      this.dialog = false
      this.$router.push({ name: 'Users' })
    },
  },
}
</script>
