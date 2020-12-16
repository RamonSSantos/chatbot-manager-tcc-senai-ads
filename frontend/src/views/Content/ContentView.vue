<template>
  <v-dialog
    v-model="dialog"
    max-width="600px"
    @click:outside="closeDialog"
    @keydown.esc="closeDialog"
    transition="slide-x-reverse-transition"
    scrollable
  >
    <v-card>
      <v-card-title>
        <span class="headline">Visualizar Conteúdo</span>
      </v-card-title>
      <v-card-text class="pb-0">
        <v-container>
          <v-expansion-panels v-model="panel" focusable>
            <v-expansion-panel>
              <v-expansion-panel-header class="text-uppercase">
                Histórico
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-row>
                  <v-col cols="12">
                    <v-text-field
                      v-model="log.createdBy"
                      label="Criado por"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="log.createdAt"
                      label="Criado em"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" v-if="log.editedBy">
                    <v-text-field
                      v-model="log.editedBy"
                      label="Editado por"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" v-if="log.editedAt">
                    <v-text-field
                      v-model="log.editedAt"
                      label="Editado em"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                </v-row>
              </v-expansion-panel-content>
            </v-expansion-panel>
            <v-expansion-panel>
              <v-expansion-panel-header class="text-uppercase">
                Informações Gerais
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-row>
                  <v-col cols="12">
                    <v-text-field
                      v-model="object.topic.description"
                      label="Tema"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="object.title"
                      label="Título"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" v-if="object.description">
                    <v-textarea
                      v-model="object.description"
                      label="Descrição"
                      :disabled="disabled"
                      rows="4"
                      auto-grow
                      filled
                    >
                    </v-textarea>
                  </v-col>
                </v-row>
              </v-expansion-panel-content>
            </v-expansion-panel>
            <v-expansion-panel>
              <v-expansion-panel-header class="text-uppercase">
                Informações de Entrada/Saída
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-list class="mt-3" elevation="2">
                  <v-subheader>Perguntas</v-subheader>
                  <v-list-item v-for="(text, i) in object.question" :key="i">
                    <v-list-item-content>
                      <v-list-item-title
                        v-text="text.description"
                      ></v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </v-list>
                <v-list class="mt-3" elevation="2">
                  <v-subheader>Respostas</v-subheader>
                  <v-list-item v-for="(text, i) in object.answer" :key="i">
                    <v-list-item-content>
                      <v-list-item-title
                        v-text="text.description"
                      ></v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </v-list>
                <v-list class="mt-3" elevation="2">
                  <v-subheader>Material anexado</v-subheader>
                  <v-list-item
                    @click.stop="downloadAttachment"
                    class="grey lighten-4"
                  >
                    <v-list-item-title
                      v-text="object.attachment"
                    ></v-list-item-title>
                    <v-list-item-icon>
                      <v-icon color="green darken-2">mdi-download</v-icon>
                    </v-list-item-icon>
                  </v-list-item>
                </v-list>
              </v-expansion-panel-content>
            </v-expansion-panel>
            <v-expansion-panel>
              <v-expansion-panel-header class="text-uppercase">
                Informações Condicionais
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-row>
                  <v-col cols="12">
                    <v-combobox
                      v-model="keywords"
                      label="Palavras-chaves"
                      :disabled="disabled"
                      multiple
                      chips
                      filled
                    >
                    </v-combobox>
                  </v-col>
                  <v-col cols="12" v-if="object.sector">
                    <v-text-field
                      v-model="object.sector.description"
                      label="Setor"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                </v-row>
              </v-expansion-panel-content>
            </v-expansion-panel>
          </v-expansion-panels>
        </v-container>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions class="pt-0">
        <v-spacer></v-spacer>
        <v-btn @click.stop="closeDialog" color="grey darken-1" text>
          Fechar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import ContentService from '@/services/ContentService'
import EventBus from '@/services/EventBus'

export default {
  name: 'ContentView',
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
    panel: 1,
    log: {
      createdBy: null,
      createdAt: null,
      editedBy: null,
      editedAt: null,
    },
    keywords: [],
  }),
  created() {
    this.dialog = true
    this.setLog()
    this.keywords = this.object.keyword.map((keyword) => keyword.description)
  },
  methods: {
    setLog() {
      const firstLog = Object.assign(
        {},
        ...this.object.log.slice(0, 1).map((item) => ({
          ['fullname']: item.user.fullname,
          ['date']: item.date,
        }))
      )
      this.log.createdBy = firstLog.fullname
      this.log.createdAt = new Date(firstLog.date).toLocaleDateString('pt-BR')

      if (this.object.log.length > 1) {
        const lastLog = this.object.log.pop()
        this.log.editedBy = lastLog.user.fullname
        this.log.editedAt = new Date(lastLog.date).toLocaleDateString('pt-BR')
      }
    },
    async downloadAttachment() {
      if (this.object.attachment !== null) {
        const response = await ContentService.downloadAttachment(
          this.object.attachment
        )

        const url = window.URL.createObjectURL(new Blob([response.data]))

        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', this.object.attachment)

        document.body.appendChild(link)
        link.click()
        link.remove()
      }
    },
    closeDialog() {
      EventBus.$emit('contents', false)
      this.dialog = false
      this.$router.push({ name: 'Contents' })
    },
  },
}
</script>
