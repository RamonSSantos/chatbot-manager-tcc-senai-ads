<template>
  <div>
    <v-card class="grey lighten-4 my-4">
      <v-card-title class="text-uppercase grey--text text--darken-1 pt-0 pl-6">
        Dashboard
      </v-card-title>
      <v-card-text>
        <v-row justify="center">
          <v-col cols="12" sm="6" md="6">
            <v-card class="grey lighten-4 my-4">
              <v-card-title class="grey--text text--darken-1 flex-column">
                Conteúdos x Temas
              </v-card-title>
              <v-card-text>
                <v-simple-table>
                  <template v-slot:default>
                    <thead>
                      <tr>
                        <th id="description" class="text-left">
                          Descrição
                        </th>
                        <th id="amount" class="text-left">
                          Quantidade
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="item in contentsAndTopics"
                        :key="item.description"
                      >
                        <td>{{ item.description }}</td>
                        <td>{{ item.amount }}</td>
                      </tr>
                    </tbody>
                  </template>
                </v-simple-table>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col cols="12" sm="6" md="6">
            <v-card class="grey lighten-4 my-4">
              <v-card-title class="grey--text text--darken-1 flex-column">
                Conteúdos x Setor
              </v-card-title>
              <v-card-text>
                <v-simple-table>
                  <template v-slot:default>
                    <thead>
                      <tr>
                        <th id="description" class="text-left">
                          Descrição
                        </th>
                        <th id="amount" class="text-left">
                          Quantidade
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="item in contentsAndSectors"
                        :key="item.description"
                      >
                        <td>{{ item.description }}</td>
                        <td>{{ item.amount }}</td>
                      </tr>
                    </tbody>
                  </template>
                </v-simple-table>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="12" sm="6" md="6">
            <v-card class="grey lighten-4 my-4">
              <v-card-title class="grey--text text--darken-1 flex-column">
                Usuários x Perfis
              </v-card-title>
              <v-card-text>
                <v-simple-table>
                  <template v-slot:default>
                    <thead>
                      <tr>
                        <th id="description" class="text-left">
                          Descrição
                        </th>
                        <th id="amount" class="text-left">
                          Quantidade
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="item in usersAndProfiles"
                        :key="item.description"
                      >
                        <td>{{ item.description }}</td>
                        <td>{{ item.amount }}</td>
                      </tr>
                    </tbody>
                  </template>
                </v-simple-table>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col cols="12" sm="6" md="6">
            <v-card class="grey lighten-4 my-4">
              <v-card-title class="grey--text text--darken-1 flex-column">
                Usuários x Setor
              </v-card-title>
              <v-card-text>
                <v-simple-table>
                  <template v-slot:default>
                    <thead>
                      <tr>
                        <th id="description" class="text-left">
                          Descrição
                        </th>
                        <th id="amount" class="text-left">
                          Quantidade
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="item in usersAndSectors"
                        :key="item.description"
                      >
                        <td>{{ item.description }}</td>
                        <td>{{ item.amount }}</td>
                      </tr>
                    </tbody>
                  </template>
                </v-simple-table>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="12" sm="6" md="6">
            <v-card class="grey lighten-4 my-4">
              <v-card-title class="grey--text text--darken-1 flex-column">
                Eventos x Status
              </v-card-title>
              <v-card-text>
                <v-simple-table>
                  <template v-slot:default>
                    <thead>
                      <tr>
                        <th id="description" class="text-left">
                          Descrição
                        </th>
                        <th id="amount" class="text-left">
                          Quantidade
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="item in logsAndStatus" :key="item.description">
                        <td>{{ item.description }}</td>
                        <td>{{ item.amount }}</td>
                      </tr>
                    </tbody>
                  </template>
                </v-simple-table>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
import ContentService from '@/services/ContentService'
import UserService from '@/services/UserService'
import MonitoringService from '@/services/MonitoringService'

export default {
  name: 'Dashboard',
  data: () => ({
    contentsAndTopics: [],
    contentsAndSectors: [],
    usersAndProfiles: [],
    usersAndSectors: [],
    logsAndStatus: [],
  }),
  created() {
    this.getContentsAndTopics()
    this.getContentsAndSectors()
    this.getUsersAndProfiles()
    this.getUsersAndSectors()
    this.getLogsAndStatus()
  },
  methods: {
    async getContentsAndTopics() {
      const response = await ContentService.getAllGroupByTopicDescription()
      this.contentsAndTopics = response.data
    },
    async getContentsAndSectors() {
      const response = await ContentService.getAllGroupBySectorDescription()
      this.contentsAndSectors = response.data
    },
    async getUsersAndProfiles() {
      const response = await UserService.getAllGroupByProfileDescription()
      this.usersAndProfiles = response.data
    },
    async getUsersAndSectors() {
      const response = await UserService.getAllGroupBySectorDescription()
      this.usersAndSectors = response.data
    },
    async getLogsAndStatus() {
      const response = await MonitoringService.getAllGroupByStatus()
      this.logsAndStatus = response.data
    },
  },
}
</script>
