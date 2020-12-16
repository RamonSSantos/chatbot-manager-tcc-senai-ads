<template>
  <div>
    <v-card v-if="listStatus" class="grey lighten-4 my-4">
      <Breadcrumb />
      <v-card-title class="text-uppercase grey--text text--darken-1 pt-0 pl-6">
        Conteúdos
      </v-card-title>
      <v-data-table
        :headers="headers"
        :items="contents"
        :search="searchContent"
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
        loading-text="Carregando... Aguarde!"
      >
        <template v-slot:top>
          <v-toolbar color="white" class="mb-4" flat>
            <v-text-field
              ref="searchContentElement"
              v-model="searchContent"
              append-icon="mdi-magnify"
              label="Informe o título do conteúdo"
              @click:append="getContentByTitle"
              @keydown.enter="getContentByTitle"
              outlined
              hide-details
            >
            </v-text-field>
            <v-spacer></v-spacer>
            <v-btn
              @click.stop="showNewObject"
              :to="{ name: 'ContentNew' }"
              color="primary"
              dark
            >
              Novo
            </v-btn>
          </v-toolbar>
        </template>
        <template v-slot:item.status="props">
          <v-edit-dialog
            :return-value.sync="props.item.status"
            @open="openEditContentStatusDialog(props.item)"
            @save="editContentStatus(props.item)"
            save-text="Salvar"
            cancel-text="Cancelar"
            large
          >
            <div v-if="props.item.status === 1">
              <v-icon color="green darken-2"
                >mdi-checkbox-marked-circle-outline</v-icon
              >
            </div>
            <div v-else-if="props.item.status === 2">
              <v-icon color="red darken-2">mdi-close-circle-outline</v-icon>
            </div>
            <template v-slot:input>
              <v-autocomplete
                v-model="contentStatus.statusDescription"
                :items="contentStatusOptions"
                :rules="[rules.required]"
                label="Status"
                no-data-text="Selecione o status"
                single-line
                clearable
              >
              </v-autocomplete>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.actions="{ item }">
          <v-btn
            @click.stop="showViewObject(item)"
            :to="{ name: 'ContentView', params: { id: +item.id } }"
            class="mr-2"
            icon
          >
            <v-icon small>
              mdi-eye
            </v-icon>
          </v-btn>
          <v-btn
            @click.stop="showEditObject(item)"
            :to="{ name: 'ContentEdit', params: { id: +item.id } }"
            icon
          >
            <v-icon small>
              mdi-pencil
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
          v-if="contents.length > 0"
          v-model="currentPage"
          :length="totalPages"
          @input="getContents"
          :total-visible="5"
          prev-icon="mdi-menu-left"
          next-icon="mdi-menu-right"
          circle
        >
        </v-pagination>
      </div>
    </v-card>
    <template v-else-if="newStatus">
      <content-new :disabled="false" />
    </template>
    <template v-if="viewStatus">
      <content-view :object="viewObject" :disabled="true" />
    </template>
    <template v-if="editStatus">
      <content-edit :object="editObject" :disabled="false" />
    </template>
  </div>
</template>

<script>
import EventBus from '@/services/EventBus'
import Breadcrumb from '@/components/breadcrumb/Breadcrumb'
import ContentService from '@/services/ContentService'
import ContentNew from './ContentNew'
import ContentView from './ContentView'
import ContentEdit from './ContentEdit'

export default {
  name: 'Contents',
  components: {
    Breadcrumb,
    ContentNew,
    ContentView,
    ContentEdit,
  },
  data: () => ({
    searchContent: '',
    isLoading: true,
    headers: [
      {
        text: 'Título',
        align: 'start',
        sortable: true,
        value: 'title',
      },
      {
        text: 'Tema',
        align: 'start',
        sortable: true,
        value: 'topic.description',
      },
      {
        text: 'Setor',
        align: 'start',
        sortable: true,
        value: 'sector.description',
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
    contents: [],
    optionsDataTable: {},
    sortBy: 'id',
    sortDesc: false,
    currentPage: 1,
    totalPages: 0,
    itemsPerPage: 5,
    totalElements: 0,
    registerNotFound: 'Nenhum registro encontrado!',
    contentStatus: {
      id: null,
      status: null,
      statusDescription: null,
    },
    contentStatusOptions: ['Ativo', 'Inativo'],
    rules: {
      required: (v) => !!v || 'Preencha este campo!',
    },
    listStatus: true,
    newStatus: false,
    viewStatus: false,
    editStatus: false,
    viewObject: {},
    editObject: {},
  }),
  created() {
    this.getContents()
  },
  mounted() {
    EventBus.$on('contents', (updateTable) => {
      this.newStatus = false
      this.viewStatus = false
      this.editStatus = false
      this.searchContent = ''

      this.listStatus = true

      if (updateTable) {
        this.getContents()
      }
    })
  },
  beforeDestroy() {
    EventBus.$off('contents')
  },
  methods: {
    async getContents() {
      this.contents = []
      this.isLoading = true

      const response = await ContentService.getAll(
        this.currentPage,
        this.sortBy,
        this.sortDesc
      )
      const { content, totalPages, totalElements } = response.data

      this.totalPages = totalPages
      this.totalElements = totalElements

      setTimeout(() => {
        this.isLoading = false
        this.contents = content
      }, 1500)
    },
    async getContentByTitle() {
      if (this.searchContent != '') {
        this.contents = []
        this.isLoading = true

        try {
          const response = await ContentService.getByTitle(this.searchContent)

          this.totalPages = 1
          setTimeout(() => {
            this.contents = response.data
            this.isLoading = false
          }, 1500)
        } catch (error) {
          this.registerNotFound = error.response.data.message
          this.isLoading = false
        }
      }
    },
    openEditContentStatusDialog(item) {
      const { id, status } = item

      this.contentStatus.id = id
      this.contentStatus.status = status

      if (this.contentStatus.status === 1) {
        this.contentStatus.statusDescription = 'Ativo'
      } else if (this.contentStatus.status === 2) {
        this.contentStatus.statusDescription = 'Inativo'
      }
    },
    async editContentStatus(item) {
      if (this.contentStatus.statusDescription) {
        this.contentStatus.status = this.updateContentStatus(
          this.contentStatus.statusDescription
        )
      }

      if (this.contentStatus.status !== item.status) {
        await ContentService.editContentStatus(
          this.contentStatus.id,
          this.contentStatus.status
        )
        this.getContents()
      }
    },
    updateContentStatus(statusDescription) {
      if (statusDescription === 'Ativo') {
        return 1
      } else if (statusDescription === 'Inativo') {
        return 2
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
    showNewObject() {
      this.listStatus = false
      this.newStatus = true
    },
    showViewObject(item) {
      this.viewStatus = true
      this.viewObject = item
    },
    showEditObject(item) {
      this.editStatus = true
      this.editObject = item
    },
  },
  watch: {
    optionsDataTable: {
      handler() {
        this.getContents()
      },
      deep: true,
    },
    searchContent(newValue) {
      if (newValue === '') {
        this.sortBy = 'id'
        this.getContents()
      }
    },
  },
}
</script>
