<template>
  <div>
    <v-card class="grey lighten-4 my-4">
      <Breadcrumb />
      <v-card-title class="text-uppercase grey--text text--darken-1 pt-0 pl-6">
        Usuários
      </v-card-title>
      <v-data-table
        :headers="headers"
        :items="users"
        :search="searchUser"
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
              ref="searchUserElement"
              v-model="searchUser"
              append-icon="mdi-magnify"
              label="Informe o nome do usuário"
              @click:append="getUserByFullname"
              @keydown.enter="getUserByFullname"
              outlined
              hide-details
            >
            </v-text-field>
            <v-spacer></v-spacer>
            <v-btn
              @click.stop="showNewObject"
              :to="{ name: 'UserNew' }"
              color="primary"
              dark
            >
              Novo
            </v-btn>
          </v-toolbar>
        </template>
        <template v-slot:item.cellphone="{ item }">
          <span>{{ item.cellphone | maskCellphone }}</span>
        </template>
        <template v-slot:item.status="props">
          <v-edit-dialog
            :return-value.sync="props.item.status"
            @open="openEditUserStatusDialog(props.item)"
            @save="editUserStatus(props.item)"
            save-text="Salvar"
            cancel-text="Cancelar"
            large
          >
            <div v-if="props.item.status === 1">
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon color="green darken-2" v-bind="attrs" v-on="on"
                    >mdi-account</v-icon
                  >
                </template>
                <span>Ativado</span>
              </v-tooltip>
            </div>
            <div v-else-if="props.item.status === 2">
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon color="red darken-2" v-bind="attrs" v-on="on"
                    >mdi-account-off</v-icon
                  >
                </template>
                <span>Desativado</span>
              </v-tooltip>
            </div>
            <div v-else-if="props.item.status === 3">
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon color="blue darken-2" v-bind="attrs" v-on="on"
                    >mdi-account-edit</v-icon
                  >
                </template>
                <span>Aguardando aprovação</span>
              </v-tooltip>
            </div>
            <template v-slot:input>
              <v-autocomplete
                v-model="userStatus.statusDescription"
                :items="sectorStatusOptions"
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
            :to="{ name: 'UserView', params: { id: +item.id } }"
            class="mr-2"
            icon
          >
            <v-icon small>
              mdi-eye
            </v-icon>
          </v-btn>
          <v-btn
            @click.stop="showEditObject(item)"
            :to="{ name: 'UserEdit', params: { id: +item.id } }"
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
          v-if="users.length > 0"
          v-model="currentPage"
          :length="totalPages"
          @input="getUsers"
          :total-visible="5"
          prev-icon="mdi-menu-left"
          next-icon="mdi-menu-right"
          circle
        ></v-pagination>
      </div>
    </v-card>
    <template v-if="newStatus">
      <user-new :disabled="false" />
    </template>
    <template v-if="viewStatus">
      <user-view :object="viewObject" :disabled="true" />
    </template>
    <template v-if="editStatus">
      <user-edit :object="editObject" :disabled="false" />
    </template>
  </div>
</template>

<script>
import EventBus from '@/services/EventBus'
import UserService from '@/services/UserService'
import Breadcrumb from '@/components/breadcrumb/Breadcrumb'
import UserNew from './UserNew'
import UserView from './UserView'
import UserEdit from './UserEdit'

export default {
  name: 'Users',
  components: {
    Breadcrumb,
    UserNew,
    UserView,
    UserEdit,
  },
  data: () => ({
    searchUser: '',
    isLoading: true,
    headers: [
      {
        text: 'Nome completo',
        align: 'start',
        sortable: true,
        value: 'fullname',
      },
      {
        text: 'E-mail',
        align: 'start',
        sortable: true,
        value: 'email',
      },
      {
        text: 'Telefone',
        align: 'start',
        sortable: false,
        value: 'cellphone',
      },
      {
        text: 'Perfil',
        align: 'start',
        sortable: true,
        value: 'profile.description',
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
    users: [],
    optionsDataTable: {},
    sortBy: 'id',
    sortDesc: false,
    currentPage: 1,
    totalPages: 0,
    itemsPerPage: 5,
    totalElements: 0,
    registerNotFound: 'Nenhum registro encontrado!',
    userStatus: {
      id: null,
      status: null,
      statusDescription: null,
    },
    sectorStatusOptions: ['Ativo', 'Inativo'],
    rules: {
      required: (v) => !!v || 'Preencha este campo!',
    },
    newStatus: false,
    viewStatus: false,
    editStatus: false,
    viewObject: {},
    editObject: {},
  }),
  created() {
    this.getUsers()
  },
  mounted() {
    this.$refs.searchUserElement.focus()
    EventBus.$on('users', (updateTable) => {
      this.newStatus = false
      this.viewStatus = false
      this.editStatus = false
      this.searchUser = ''

      if (updateTable) {
        this.getUsers()
      }
    })
  },
  beforeDestroy() {
    EventBus.$off('users')
  },
  methods: {
    async getUsers() {
      this.users = []
      this.isLoading = true

      const response = await UserService.getAll(
        this.currentPage,
        this.sortBy,
        this.sortDesc
      )
      const { content, totalPages, totalElements } = response.data

      this.totalPages = totalPages
      this.totalElements = totalElements
      setTimeout(() => {
        this.isLoading = false
        this.users = content
      }, 1500)
    },
    async getUserByFullname() {
      if (this.searchUser !== '') {
        this.users = []
        this.isLoading = true

        try {
          const response = await UserService.getByFullname(this.searchUser)

          this.totalPages = 1
          setTimeout(() => {
            this.isLoading = false
            this.users = response.data
          }, 1500)
        } catch (error) {
          this.registerNotFound = error.response.data.message
          this.isLoading = false
        }
      }
    },
    openEditUserStatusDialog(item) {
      const { id, status } = item

      this.userStatus.id = id
      this.userStatus.status = status

      if (this.userStatus.status === 1) {
        this.userStatus.statusDescription = 'Ativo'
      } else if (this.userStatus.status === 2) {
        this.userStatus.statusDescription = 'Inativo'
      }
    },
    async editUserStatus(item) {
      if (this.userStatus.statusDescription) {
        this.userStatus.status = this.updateUserStatus(
          this.userStatus.statusDescription
        )

        if (this.userStatus.status !== item.status) {
          await UserService.editUserStatus(
            this.userStatus.id,
            this.userStatus.status
          )
          this.getUsers()
        }
      }
    },
    updateUserStatus(statusDescription) {
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
        this.getUsers()
      },
      deep: true,
    },
    searchUser(newValue) {
      if (newValue === '') {
        this.sortBy = 'id'
        this.getUsers()
      }
    },
  },
}
</script>
