<template>
  <v-card>
    <v-card-title>
      Setores
      <v-spacer></v-spacer>
      <v-dialog
        v-model="newSectorDialog"
        max-width="600px"
        @click:outside="closeNewSectorDialog"
        @keydown.esc="closeNewSectorDialog"
        transition="slide-x-reverse-transition"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="primary" v-bind="attrs" v-on="on" dark>
            Novo
          </v-btn>
        </template>
        <v-card>
          <v-card-title>
            <span class="headline">Novo Setor</span>
          </v-card-title>
          <v-card-text class="pb-0">
            <v-container class="pb-0">
              <v-form ref="newSector" v-model="valid">
                <v-row>
                  <v-col cols="12" sm="7" md="8">
                    <v-text-field
                      v-model="newSector.description"
                      :rules="[rules.sectorLength]"
                      label="Descrição"
                      outlined
                      required
                      autofocus
                    >
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" sm="5" md="4">
                    <v-autocomplete
                      v-model="newSector.statusDescription"
                      :items="sectorStatus"
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
              @click.stop="newSectorDialog = false"
              color="grey darken-1"
              text
            >
              Fechar
            </v-btn>
            <v-spacer></v-spacer>
            <v-btn @click="resetNewSectorDialog" color="warning darken-1" text>
              Limpar
            </v-btn>
            <v-btn
              @click.stop="addSector"
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
        :items="sectors"
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
                v-model="editSector.description"
                :rules="[rules.sectorLength]"
                label="Descrição"
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
              <v-icon color="green darken-2"
                >mdi-checkbox-marked-circle-outline</v-icon
              >
            </div>
            <div v-else-if="props.item.status === 2">
              <v-icon color="red darken-2">mdi-close-circle-outline</v-icon>
            </div>
            <template v-slot:input>
              <v-autocomplete
                v-model="editSector.statusDescription"
                :items="sectorStatus"
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
          v-if="sectors.length > 0"
          v-model="currentPage"
          :length="totalPages"
          @input="getSectors"
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
import SectorService from '@/services/SectorService'

export default {
  name: 'Sectors',
  data: () => ({
    isLoading: null,
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
    sectors: [],
    optionsDataTable: {},
    sortBy: 'description',
    sortDesc: false,
    currentPage: 1,
    totalPages: 0,
    itemsPerPage: 10,
    totalElements: 0,
    newSectorDialog: false,
    valid: true,
    newSector: {
      description: null,
      status: null,
      statusDescription: null,
    },
    editSector: {
      id: null,
      description: null,
      status: null,
      statusDescription: null,
    },
    sectorStatus: ['Ativo', 'Inativo'],
    rules: {
      required: (v) => !!v || 'Preencha este campo!',
      sectorLength: (v) =>
        (v && v.length >= 2) || 'O setor deve ter pelo menos 2 caracteres',
    },
  }),
  created() {
    this.getSectors()
  },
  methods: {
    async getSectors() {
      this.sectors = []
      this.isLoading = true

      const response = await SectorService.getAll(
        this.currentPage,
        this.sortBy,
        this.sortDesc
      )
      const { content, totalPages, totalElements } = response.data

      this.totalPages = totalPages
      this.totalElements = totalElements
      setTimeout(() => {
        this.isLoading = false
        this.sectors = content
      }, 1500)
    },
    async addSector() {
      if (this.$refs.newSector.validate()) {
        this.newSector.status = this.updateSectorStatus(
          this.newSector.statusDescription
        )

        await SectorService.new(this.newSector)
        this.resetFields()
        this.closeNewSectorDialog()
        this.getSectors()
      }
    },
    openDialog(item) {
      const { id, description, status } = item

      this.editSector.id = id
      this.editSector.description = description
      this.editSector.status = status

      if (this.editSector.status === 1) {
        this.editSector.statusDescription = 'Ativo'
      } else if (this.editSector.status === 2) {
        this.editSector.statusDescription = 'Inativo'
      }
    },
    async edit(item) {
      if (this.editSector.statusDescription) {
        this.editSector.status = this.updateSectorStatus(
          this.editSector.statusDescription
        )
        if (
          (this.editSector.description !== item.description &&
            this.editSector.description.length >= 2) ||
          this.editSector.status !== item.status
        ) {
          await SectorService.edit(this.editSector)
          this.getSectors()
        }
      }
    },
    updateSectorStatus(statusDescription) {
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
    closeNewSectorDialog() {
      this.newSectorDialog = false
    },
    resetNewSectorDialog() {
      this.$refs.newSector.reset()
    },
    resetFields() {
      this.$refs.newSector.resetValidation()

      this.newSector.description = null
      this.newSector.status = null
      this.newSector.statusDescription = null
    },
  },
  watch: {
    optionsDataTable: {
      handler() {
        this.getSectors()
      },
      deep: true,
    },
  },
}
</script>
