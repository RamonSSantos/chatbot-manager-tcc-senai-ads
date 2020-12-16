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
        <span class="headline">Visualizar Evento</span>
      </v-card-title>
      <v-card-text class="pb-0">
        <v-container>
          <v-expansion-panels v-model="panel" focusable>
            <v-expansion-panel>
              <v-expansion-panel-header class="text-uppercase">
                Linha do Tempo
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-row>
                  <v-timeline>
                    <v-timeline-item
                      v-for="item in logs"
                      :key="item.id"
                      :color="item.color"
                      small
                    >
                      <template v-slot:opposite>
                        <span
                          class="subtitle-1 font-weight-bold"
                          v-text="
                            item.date.getDate() +
                            '/' +
                            (item.date.getMonth() + 1) +
                            '/' +
                            item.date.getFullYear() +
                            ' ' +
                            item.date.getHours() +
                            ':' +
                            item.date.getMinutes()
                          "
                        >
                        </span>
                      </template>
                      <v-card class="elevation-2">
                        <v-card-title class="subtitle-1 pb-0">
                          <div v-if="item.type === 'content'">
                            <div v-if="item.status === 1">
                              Conteúdo criado
                            </div>
                            <template v-else-if="item.status === 2">
                              Conteúdo editado
                            </template>
                          </div>
                          <template v-else-if="item.type === 'event'">
                            <div v-if="item.status === 1">
                              Conteúdo processado pelo Chatbot
                            </div>
                            <template v-else-if="item.status === 2">
                              Conteúdo enviado
                            </template>
                            <template v-else-if="item.status === 3">
                              Fluxo reiniciado
                            </template>
                            <template v-else-if="item.status === 4">
                              Evento cancelado
                            </template>
                            <template v-else-if="item.status === 5">
                              Conteúdo não enviado
                            </template>
                          </template>
                          <template v-else-if="item.type === 'message'">
                            <div v-if="item.status === 1">
                              Pergunta enviada pelo Chatbot
                            </div>
                            <template v-else-if="item.status === 2">
                              Resposta enviada ao Chatbot
                            </template>
                          </template>
                        </v-card-title>
                        <v-card-text>
                          <div v-if="item.type === 'content'">
                            Por: {{ item.fullname }}
                          </div>
                          <template v-else-if="item.type === 'event'">
                            <div v-if="item.status === 1">
                              Conteúdo entrou na fila
                            </div>
                            <template v-else-if="item.status === 2">
                              Para: {{ item.fullname }}
                            </template>
                            <template
                              v-else-if="item.status === 3 || item.status === 4"
                            >
                              Por: {{ item.fullname }}
                            </template>
                            <template v-else-if="item.status === 5">
                              Resposta do usuário não combinou com as condições
                              do conteúdo
                            </template>
                          </template>
                          <template v-else-if="item.type === 'message'">
                            <div v-if="item.status === 1">
                              Para: {{ item.fullname }}
                            </div>
                            <template v-else-if="item.status === 2">
                              Por: {{ item.fullname }}
                            </template>
                          </template>
                        </v-card-text>
                      </v-card>
                    </v-timeline-item>
                  </v-timeline>
                </v-row>
              </v-expansion-panel-content>
            </v-expansion-panel>
            <v-expansion-panel>
              <v-expansion-panel-header class="text-uppercase">
                Informações do Conteúdo
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-row>
                  <v-col cols="12">
                    <v-text-field
                      v-model="object.content.topic.description"
                      label="Tema"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-text-field
                      v-model="object.content.title"
                      label="Título"
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
                Informações do Usuário
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-row>
                  <v-col cols="12">
                    <v-text-field
                      v-model="object.user.fullname"
                      label="Nome completo"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6" md="6">
                    <v-text-field
                      v-model="object.user.email"
                      label="E-mail"
                      :disabled="disabled"
                      filled
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6" md="6">
                    <v-text-field
                      v-model="object.user.sector.description"
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
        <v-dialog v-model="cancelEventModal" max-width="350" persistent>
          <template #activator="{ on: dialog, attrs }">
            <v-tooltip top>
              <template #activator="{ on: tooltip }">
                <v-btn
                  :disabled="statusCancelEvent"
                  color="red darken-1"
                  v-bind="attrs"
                  v-on="{ ...dialog, ...tooltip }"
                  text
                >
                  Cancelar
                </v-btn>
              </template>
              <span>Altera o status do evento para cancelado</span>
            </v-tooltip>
          </template>
          <v-card>
            <v-card-title class="headline">
              Deseja cancelar o evento?
            </v-card-title>
            <v-card-text>
              O cancelamento do evento garante que o usuário não receba esse
              conteúdo
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="grey darken-1"
                @click.stop="cancelEventModal = false"
                text
              >
                Não
              </v-btn>
              <v-btn color="blue darken-1" @click.stop="cancelEvent" text>
                Sim
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog v-model="resetEventModal" max-width="350" persistent>
          <template #activator="{ on: dialog, attrs }">
            <v-tooltip top>
              <template #activator="{ on: tooltip }">
                <v-btn
                  :disabled="statusResetEvent"
                  color="blue darken-1"
                  v-bind="attrs"
                  v-on="{ ...dialog, ...tooltip }"
                  text
                >
                  Reiniciar
                </v-btn>
              </template>
              <span>Reinicia o fluxo do evento</span>
            </v-tooltip>
          </template>
          <v-card>
            <v-card-title class="headline">
              Deseja reiniciar o evento?
            </v-card-title>
            <v-card-text>
              Reiniciando o fluxo do evento altera o status para agendado
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="grey darken-1"
                @click.stop="resetEventModal = false"
                text
              >
                Não
              </v-btn>
              <v-btn color="blue darken-1" @click.stop="resetEvent" text>
                Sim
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-spacer></v-spacer>
        <v-btn @click.stop="closeDialog(false)" color="grey darken-1" text>
          Fechar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import EventService from '@/services/EventService'
import LogService from '@/services/LogService'
import EventBus from '@/services/EventBus'

export default {
  name: 'EventView',
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
    statusCancelEvent() {
      return this.object.status === 1 ? false : true
    },
    statusResetEvent() {
      return this.object.status === 1 ? true : false
    },
  },
  data: () => ({
    dialog: false,
    panel: 0,
    eventLog: [],
    contentLog: [],
    messageLog: [],
    logs: [],
    cancelEventModal: false,
    resetEventModal: false,
  }),
  created() {
    this.dialog = true
    this.getLogs()
  },
  methods: {
    async getLogs() {
      this.eventLog = this.object.log.slice().map((item) => ({
        ['id']: item.id,
        ['type']: 'event',
        ['date']: new Date(item.date),
        ['status']: item.status,
        ['fullname']: item.user.fullname,
        ['color']:
          item.status === 1
            ? 'grey'
            : item.status === 2
            ? 'green'
            : item.status === 3
            ? 'blue'
            : 'red',
      }))

      this.contentLog = this.object.content.log.slice().map((item) => ({
        ['id']: item.id,
        ['type']: 'content',
        ['date']: new Date(item.date),
        ['status']: item.status,
        ['fullname']: item.user.fullname,
        ['color']: 'grey',
      }))

      const response = await LogService.getByUserIdAndMessageContentId(
        this.object.user.id,
        this.object.content.id
      )
      this.messageLog = response.data

      this.messageLog = this.messageLog.slice().map((item) => ({
        ['id']: item.id,
        ['type']: 'message',
        ['date']: new Date(item.date),
        ['status']: item.status,
        ['fullname']: item.userFullname,
        ['color']: 'grey',
      }))

      this.logs = this.eventLog
        .concat(this.contentLog, this.messageLog)
        .slice()
        .sort((a, b) => {
          return a.date.getTime() < b.date.getTime()
            ? -1
            : a.date.getTime() > b.date.getTime()
            ? 1
            : 0
        })
    },
    cancelEvent() {
      this.cancelEventModal = false
      this.editStatus(4)
    },
    resetEvent() {
      this.resetEventModal = false
      this.editStatus(3)
    },
    async editStatus(status) {
      await EventService.editStatus(this.object.id, status)
      this.closeDialog(true)
    },
    closeDialog(updateTable) {
      EventBus.$emit('events', updateTable)
      this.dialog = false
      this.$router.push({ name: 'Events' })
    },
  },
}
</script>
