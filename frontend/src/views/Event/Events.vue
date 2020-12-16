<template>
  <div>
    <v-card class="grey lighten-4 my-4">
      <BreadCrumb />
      <v-card-title class="text-uppercase grey--text text--darken-1 pt-0 pl-6">
        Eventos
      </v-card-title>
      <v-data-table
        :headers="headers"
        :items="events"
        :search="searchEvent"
        :options.sync="optionsDataTable"
        :sort-by.sync="sortBy"
        :sort-desc.sync="sortDesc"
        @update:options="handleOptionsUpdate"
        item-key="id"
        fixed-header
        hide-default-footer
        :page.sync="currentPage"
        :items-per-page="itemsPerPage"
        :server-items-length="totalElements"
        @page-count="totalPages = $event"
        :loading="isLoading"
        loading-text="Carregando... Aguarde"
      >
        <template v-slot:top>
          <v-toolbar color="white" class="mb-4" flat>
            <v-text-field
              ref="searchEventElement"
              v-model="searchEvent"
              append-icon="mdi-magnify"
              label="Informe o título do conteúdo"
              @click:append="getEventByTitle"
              @keydown.enter="getEventByTitle"
              outlined
              hide-details
            >
            </v-text-field>
          </v-toolbar>
        </template>
        <template v-slot:item.status="props">
          <div v-if="props.item.status === 1">
            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <v-icon v-bind="attrs" v-on="on">
                  mdi-calendar-clock
                </v-icon>
              </template>
              <span>Está na fila para ser enviado</span>
            </v-tooltip>
          </div>
          <div v-else-if="props.item.status === 2">
            <v-tooltip right>
              <template v-slot:activator="{ on, attrs }">
                <v-icon color="green darken-2" v-bind="attrs" v-on="on">
                  mdi-send-check
                </v-icon>
              </template>
              <span>Enviado</span>
            </v-tooltip>
          </div>
          <div v-else-if="props.item.status === 4">
            <v-tooltip right>
              <template v-slot:activator="{ on, attrs }">
                <v-icon color="red darken-2" v-bind="attrs" v-on="on">
                  mdi-send-lock
                </v-icon>
              </template>
              <span>Cancelado</span>
            </v-tooltip>
          </div>
          <div v-else-if="props.item.status === 5">
            <v-tooltip right>
              <template v-slot:activator="{ on, attrs }">
                <v-icon color="orange darken-2" v-bind="attrs" v-on="on">
                  mdi-send-lock
                </v-icon>
              </template>
              <span>Incompleto</span>
            </v-tooltip>
          </div>
        </template>
        <template v-slot:item.actions="{ item }">
          <v-btn
            @click.stop="showViewObject(item)"
            :to="{ name: 'EventView', params: { id: +item.id } }"
            icon
          >
            <v-icon small>
              mdi-eye
            </v-icon>
          </v-btn>
        </template>
        <template v-slot:no-data>
          {{ registerNotFound }}
        </template>

        <template v-slot:no-results>
          {{ registerNotFound }}
        </template>
      </v-data-table>
      <div class="text-center py-2" v-if="!isLoading">
        <v-pagination
          v-if="events.length > 0"
          v-model="currentPage"
          :length="totalPages"
          @input="getEvents"
          :total-visible="5"
          prev-icon="mdi-menu-left"
          next-icon="mdi-menu-right"
          circle
        >
        </v-pagination>
      </div>
    </v-card>
    <template v-if="viewStatus">
      <event-view :object="viewObject" :disabled="true" />
    </template>
  </div>
</template>

<script>
import BreadCrumb from '@/components/breadcrumb/Breadcrumb'
import EventBus from '@/services/EventBus'
import EventService from '@/services/EventService'
import EventView from './EventView'

export default {
  name: 'Events',
  components: {
    BreadCrumb,
    EventView,
  },
  data: () => ({
    searchEvent: '',
    isLoading: true,
    headers: [
      {
        text: 'Título',
        align: 'start',
        sortable: true,
        value: 'content.title',
      },
      {
        text: 'Tema',
        align: 'start',
        sortable: true,
        value: 'content.topic.description',
      },
      {
        text: 'Usuário',
        align: 'start',
        sortable: true,
        value: 'user.fullname',
      },
      {
        text: 'Setor do Usuário',
        align: 'start',
        sortable: true,
        value: 'user.sector.description',
      },
      {
        text: 'Status',
        align: 'start',
        sortable: true,
        value: 'status',
      },
      {
        text: 'Ações',
        align: 'start',
        sortable: false,
        value: 'actions',
      },
    ],
    events: [],
    optionsDataTable: {},
    sortBy: 'id',
    sortDesc: false,
    currentPage: 1,
    totalPages: 0,
    itemsPerPage: 5,
    totalElements: 0,
    registerNotFound: 'Nenhum registro encontrado!',
    viewStatus: false,
    viewObject: {},
  }),
  created() {
    this.getEvents()
  },
  mounted() {
    this.$refs.searchEventElement.focus()

    EventBus.$on('events', (updateTable) => {
      this.viewStatus = false
      this.searchEvent = ''

      if (updateTable) {
        this.getEvents()
      }
    })
  },
  beforeDestroy() {
    EventBus.$off('events')
  },
  methods: {
    async getEvents() {
      this.events = []
      this.isLoading = true

      const response = await EventService.getAll(
        this.currentPage,
        this.sortBy,
        this.sortDesc
      )
      const { content, totalPages, totalElements } = response.data

      this.totalPages = totalPages
      this.totalElements = totalElements
      setTimeout(() => {
        this.events = content
        this.isLoading = false
      }, 1500)
    },
    async getEventByTitle() {
      if (this.searchEvent !== '') {
        this.events = []
        this.isLoading = true

        try {
          const response = await EventService.getByContentTitle(
            this.searchEvent
          )

          this.totalPages = 1
          setTimeout(() => {
            this.isLoading = false
            this.events = response.data
          }, 1500)
        } catch (error) {
          this.registerNotFound = error.response.data.message
          this.isLoading = false
        }
      }
    },
    handleOptionsUpdate({ sortBy, sortDesc }) {
      if (sortBy[0] != undefined) {
        this.sortBy = sortBy[0]
      }
      if (sortDesc[0] != undefined) {
        this.sortDesc = sortDesc[0]
      }
    },
    showViewObject(item) {
      this.viewStatus = true
      this.viewObject = item
    },
  },
  watch: {
    optionsDataTable: {
      handler() {
        this.getEvents()
      },
      deep: true,
    },
    searchEvent(newValue) {
      if (newValue === '') {
        this.sortBy = 'id'
        this.getEvents()
      }
    },
  },
}
</script>
