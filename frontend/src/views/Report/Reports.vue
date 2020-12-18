<template>
  <v-card class="grey lighten-4 my-4 pb-4">
    <Breadcrumb />
    <v-card-title
      class="text-uppercase grey--text text--darken-1 pt-0 pl-6 flex-column"
    >
      Eventos por Status
    </v-card-title>
    <v-card-subtitle class="text-center">
      <v-row justify="center">
        <v-col cols="12" sm="12" md="3">
          <v-autocomplete
            v-model="logStatus"
            :items="listStatus"
            item-text="description"
            :rules="[rules.required]"
            label="Status*"
            no-data-text="Selecione algum status"
            return-object
            clearable
            filled
            required
          >
          </v-autocomplete>
        </v-col>
        <v-col cols="12" sm="6" md="3">
          <v-menu
            ref="startingDate"
            v-model="startingDate"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            max-width="290px"
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="startDate"
                label="Data inicial*"
                prepend-icon="mdi-calendar"
                v-bind="attrs"
                :rules="[rules.required]"
                @blur="dateStart = parseDate(startDate)"
                v-on="on"
                clearable
                filled
                required
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="dateStart"
              no-title
              @input="startingDate = false"
            ></v-date-picker>
          </v-menu>
        </v-col>
        <v-col cols="12" sm="6" md="3">
          <v-menu
            ref="endingDate"
            v-model="endingDate"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            max-width="290px"
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="endDate"
                label="Data final*"
                prepend-icon="mdi-calendar"
                v-bind="attrs"
                :rules="[rules.required]"
                @blur="dateEnd = parseDate(endDate)"
                v-on="on"
                clearable
                filled
                required
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="dateEnd"
              no-title
              @input="endingDate = false"
            ></v-date-picker>
          </v-menu>
        </v-col>
        <v-col cols="12" sm="6" md="3">
          <v-btn
            class="ma-2"
            :loading="loading"
            :disabled="statusButton"
            color="info"
            @click="getStatusLogsByStatusAndDate"
          >
            Pesquisar
          </v-btn>
        </v-col>
      </v-row>
    </v-card-subtitle>
    <v-card-text class="pb-0" v-if="chartData">
      <v-card class="grey lighten-4 my-4">
        <v-card-title class="grey--text text--darken-1 flex-column">
          Eventos por Setor
        </v-card-title>
        <v-card-text>
          <bar-chart :chart-data="chartData"></bar-chart>
        </v-card-text>
      </v-card>
    </v-card-text>
  </v-card>
</template>

<script>
import Breadcrumb from '@/components/breadcrumb/Breadcrumb'
import BarChart from './BarChart.js'
import MonitoringService from '@/services/MonitoringService'

export default {
  name: 'Reports',
  components: {
    Breadcrumb,
    BarChart,
  },
  computed: {
    statusButton: {
      get() {
        return this.logStatus === null ||
          this.startDate === null ||
          this.endDate === null
          ? true
          : false
      },
      set(newValue) {
        return newValue
      },
    },
    computedDateFormatted() {
      return this.formatDate(this.date)
    },
  },
  data: (vm) => ({
    logStatus: null,
    listStatus: [],
    startingDate: false,
    endingDate: false,
    dateStart: new Date().toISOString().substr(0, 10),
    dateEnd: new Date().toISOString().substr(0, 10),
    startDate: vm.formatDate(new Date().toISOString().substr(0, 10)),
    endDate: vm.formatDate(new Date().toISOString().substr(0, 10)),
    loader: null,
    loading: false,
    chartData: null,
    rules: {
      required: (v) => !!v || 'Preencha este campo!',
    },
  }),
  created() {
    this.getListStatus()
  },
  mounted() {
    this.getChartData()
  },
  watch: {
    dateStart(val) {
      this.startDate = this.formatDate(val)
    },
    dateEnd(val) {
      this.endDate = this.formatDate(val)
    },
    loader(newValue) {
      const l = newValue
      this[l] = !this[l]

      setTimeout(() => (this[l] = false), 2000)

      this.loader = null
    },
  },
  methods: {
    async getListStatus() {
      const response = await MonitoringService.getAllMonitoringLogStatusEnum()
      this.listStatus = response.data
    },
    async getStatusLogsByStatusAndDate() {
      if (
        this.logStatus !== null &&
        this.startDate !== null &&
        this.endDate !== null
      ) {
        this.statusButton = true

        let fromDate = new Date(this.parseDate(this.startDate))
        let toDate = new Date(this.parseDate(this.endDate))

        const response = await MonitoringService.getAllGroupBySectorDescription(
          this.logStatus.id,
          fromDate.toISOString().substr(0, 10),
          toDate.toISOString().substr(0, 10)
        )

        let data = response.data

        if (data.length > 0) {
          this.loader = 'loading'
          this.statusButton = false

          this.getChartData(response.data)
        } else {
          const notification = {
            type: 'green darken-2',
            message: 'Nenhum registro encontrado!',
            icon: 'mdi-checkbox-marked-circle',
          }
          this.$store.dispatch('notification/add', notification, { root: true })
        }
      }
    },
    getChartData(data) {
      if (data) {
        let descriptions = data.map((item) => item.description)
        let amounts = data.map((item) => item.amount)

        this.chartData = {
          labels: descriptions,
          datasets: [
            {
              label: 'Quantidade',
              borderWidth: 1,
              backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
              ],
              borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)',
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
              ],
              data: amounts,
            },
          ],
        }
      }
    },
    formatDate(date) {
      if (!date) return null

      const [year, month, day] = date.split('-')
      return `${day}/${month}/${year}`
    },
    parseDate(date) {
      if (!date) return null

      const [day, month, year] = date.split('/')
      return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`
    },
  },
}
</script>
