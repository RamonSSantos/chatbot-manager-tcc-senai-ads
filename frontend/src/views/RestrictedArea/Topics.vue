<template>
  <v-card>
    <v-card-title>
      Temas
      <v-spacer></v-spacer>
      <v-dialog
        v-model="newTopicDialog"
        max-width="600px"
        @click:outside="closeNewTopicDialog"
        @keydown.esc="closeNewTopicDialog"
        transition="slide-x-reverse-transition"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="primary" v-bind="attrs" v-on="on" dark>
            Novo
          </v-btn>
        </template>
        <v-card>
          <v-card-title>
            <span class="headline">Novo Tema</span>
          </v-card-title>
          <v-card-text class="pb-0">
            <v-container class="pb-0">
              <v-form ref="newTopic" v-model="valid">
                <v-row>
                  <v-col cols="12" sm="7" md="8">
                    <v-text-field
                      v-model="newTopic.description"
                      :rules="[rules.topicLength]"
                      label="Descrição"
                      outlined
                      required
                      autofocus
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" sm="5" md="4">
                    <v-autocomplete
                      v-model="newTopic.statusDescription"
                      :items="topicStatus"
                      :rules="[rules.required]"
                      label="Status"
                      no-data-text="Selecione o status"
                      clearable
                      outlined
                    >
                    </v-autocomplete>
                  </v-col>
                </v-row>
              </v-form>
            </v-container>
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions class="pt-0">
            <v-btn
              @click.stop="newTopicDialog = false"
              color="grey darken-1"
              text
            >
              Fechar
            </v-btn>
            <v-spacer></v-spacer>
            <v-btn @click="resetNewTopicDialog" color="warning darken-1" text>
              Limpar
            </v-btn>
            <v-btn
              @click.stop="addTopic"
              :disabled="!valid"
              color="blue darken-1"
              text
            >
              Criar
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-card-title>
    <v-card-text>
      <v-data-table
        :headers="headers"
        :items="topics"
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
        <template v-slot:item.description="props">
          <v-edit-dialog
            :return-value.sync="props.item.description"
            @open="openDialog(props.item)"
            @save="edit(props.item)"
            save-text="Salvar"
            cancel-text="Cancelar"
            large
          >
            {{ props.item.description }}
            <template v-slot:input>
              <v-text-field
                v-model="editTopic.description"
                :rules="[rules.topicLength]"
                label="Tema"
                single-line
              >
              </v-text-field>
            </template>
          </v-edit-dialog>
        </template>
        <template v-slot:item.status="props">
          <v-edit-dialog
            :return-value.sync="props.item.status"
            @open="openDialog(props.item)"
            @save="edit(props.item)"
            save-text="Salvar"
            cancel-text="Cancelar"
            large
          >
            <div v-if="props.item.status === 1">
              <v-icon color="green darken-2">
                mdi-checkbox-marked-circle-outline
              </v-icon>
            </div>
            <div v-else-if="props.item.status === 2">
              <v-icon color="red darken-2">mdi-close-circle-outline</v-icon>
            </div>
            <template v-slot:input>
              <v-autocomplete
                v-model="editTopic.statusDescription"
                :items="topicStatus"
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
        <template v-slot:no-data>
          Nenhum registro encontrado!
        </template>

        <template v-slot:no-results>
          Nenhum registro encontrado!
        </template>
      </v-data-table>
      <div class="text-center py-2" v-if="!isLoading">
        <v-pagination
          v-if="topics.length > 0"
          v-model="currentPage"
          :length="totalPages"
          @input="getTopics"
          :total-visible="5"
          prev-icon="mdi-menu-left"
          next-icon="mdi-menu-right"
          circle
        >
        </v-pagination>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import TopicService from '@/services/TopicService'

export default {
  name: 'Topics',
  data: () => ({
    isLoading: true,
    headers: [
      {
        text: 'ID',
        align: 'start',
        sortable: true,
        value: 'id',
      },
      {
        text: 'Descrição',
        align: 'start',
        sortable: true,
        value: 'description',
      },
      {
        text: 'Status',
        align: 'start',
        sortable: true,
        value: 'status',
      },
    ],
    topics: [],
    optionsDataTable: {},
    sortBy: 'description',
    sortDesc: false,
    currentPage: 1,
    totalPages: 0,
    itemsPerPage: 10,
    totalElements: 0,
    newTopicDialog: false,
    valid: true,
    newTopic: {
      description: null,
      status: null,
      statusDescription: null,
    },
    editTopic: {
      id: null,
      description: null,
      status: null,
      statusDescription: null,
    },
    topicStatus: ['Ativo', 'Inativo'],
    rules: {
      required: (v) => !!v || 'Preencha este campo!',
      topicLength: (v) =>
        (v && v.length >= 4) || 'O tema deve ter pelo menos 4 caracteres',
    },
  }),
  created() {
    this.getTopics()
  },
  methods: {
    async getTopics() {
      this.topics = []
      this.isLoading = true

      const response = await TopicService.getAll(
        this.currentPage,
        this.sortBy,
        this.sortDesc
      )
      const { content, totalPages, totalElements } = response.data

      this.totalPages = totalPages
      this.totalElements = totalElements
      setTimeout(() => {
        this.isLoading = false
        this.topics = content
      }, 1500)
    },
    async addTopic() {
      if (this.$refs.newTopic.validate()) {
        this.newTopic.status = this.updateTopicStatus(
          this.newTopic.statusDescription
        )

        await TopicService.new(this.newTopic)
        this.resetFields()
        this.closeNewTopicDialog()
        this.getTopics()
      }
    },
    openDialog(item) {
      const { id, description, status } = item

      this.editTopic.id = id
      this.editTopic.description = description
      this.editTopic.status = status

      if (this.editTopic.status === 1) {
        this.editTopic.statusDescription = 'Ativo'
      } else if (this.editTopic.status === 2) {
        this.editTopic.statusDescription = 'Inativo'
      }
    },
    async edit(item) {
      if (this.editTopic.statusDescription) {
        this.editTopic.status = this.updateTopicStatus(
          this.editTopic.statusDescription
        )
      }
      if (
        (this.editTopic.description !== item.description &&
          this.editTopic.description.length >= 4) ||
        this.editTopic.status !== item.status
      ) {
        await TopicService.edit(this.editTopic)
        this.getTopics()
      }
    },
    updateTopicStatus(statusDescription) {
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
    closeNewTopicDialog() {
      this.newTopicDialog = false
    },
    resetNewTopicDialog() {
      this.$refs.newTopic.reset()
    },
    resetFields() {
      this.$refs.newTopic.resetValidation()

      this.newTopic.description = null
      this.newTopic.status = null
      this.newTopic.statusDescription = null
    },
  },
  watch: {
    optionsDataTable: {
      handler() {
        this.getTopics()
      },
      deep: true,
    },
  },
}
</script>
