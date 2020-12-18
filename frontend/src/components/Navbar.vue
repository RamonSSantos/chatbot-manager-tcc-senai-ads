<template>
  <nav>
    <v-app-bar color="blue darken-2" text dark>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title class="grey--text text--lighten-5">
        <span class="font-weight-medium">
          chatbot
        </span>
        <span class="font-weight-light">
          Manager
        </span>
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <!--<v-menu offset-y>
        <template v-slot:activator="{ on, attrs }">
          <v-btn text color="grey lighten-5" v-bind="attrs" v-on="on">
            <v-icon left>mdi-menu-down</v-icon>
            <span>Menu</span>
          </v-btn>
        </template>
        <v-list>
          <v-list-item
            v-for="item in items"
            :key="item.title"
            router
            :to="item.route"
            active-class="light-blue--text text--darken-4"
          >
            <v-list-item-title> {{ item.title }} </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>-->
      <v-dialog v-model="logoutModal" max-width="320" persistent>
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="grey lighten-5" v-bind="attrs" v-on="on" text>
            <span>Sair</span>
            <v-icon right>mdi-export</v-icon>
          </v-btn>
        </template>
        <v-card>
          <v-card-title class="headline">
            Sair do sistema
          </v-card-title>
          <v-card-text>
            Tem certeza que deseja sair do sistema?
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey darken-1" @click.stop="logoutModal = false" text>
              Não
            </v-btn>
            <v-btn color="blue darken-1" @click.stop.prevent="logout" text>
              Sim
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" color="blue darken-2" app temporary>
      <v-layout column align-center>
        <v-flex class="text-center mt-5">
          <v-avatar size="50" color="grey lighten-5">
            <span class="blue-grey--text headline">{{ avatarText }}</span>
          </v-avatar>
          <p
            class="reset-line-height text-button grey--text text--lighten-5 mt-2 mb-0"
          >
            {{ givenNameAndSurname }}
          </p>
          <span class="text-body-2 grey--text text--lighten-2">
            {{ email }}
          </span>
        </v-flex>
      </v-layout>
      <v-list>
        <v-list-item-group active-class="light-blue--text text--accent-1">
          <v-list-item
            v-for="item in items"
            :key="item.title"
            router
            :to="{ name: item.name }"
          >
            <v-list-item-icon>
              <v-icon
                v-text="item.icon"
                class="grey--text text--lighten-2"
              ></v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title
                v-text="item.title"
                class="text-uppercase grey--text text--lighten-2"
              >
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-navigation-drawer>
  </nav>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Navbar',
  data: () => ({
    drawer: false,
    items: [
      { title: 'Dashboard', icon: 'mdi-view-dashboard', name: 'Dashboard' },
      { title: 'Meu Perfil', icon: 'mdi-account-edit', name: 'MyProfile' },
      { title: 'Usuários', icon: 'mdi-account-multiple', name: 'Users' },
      {
        title: 'Conteúdos',
        icon: 'mdi-file-document-multiple',
        name: 'Contents',
      },
      { title: 'Eventos', icon: 'mdi-table-eye', name: 'Events' },
      { title: 'Relatórios', icon: 'mdi-chart-arc', name: 'Reports' },
      {
        title: 'Área Restrita',
        icon: 'mdi-account-lock',
        name: 'Profiles',
      },
    ],
    avatarText: null,
    givenNameAndSurname: null,
    email: null,
    logoutModal: false,
  }),
  created() {
    this.getUser()
    this.setAvatarText()
  },
  methods: {
    ...mapActions(['LogOut']),
    getUser() {
      const response = this.$store.getters['stateUser']
      const authentication = JSON.parse(response)

      this.givenNameAndSurname = authentication.userFullname
      this.email = authentication.userEmail
    },
    async logout() {
      this.logoutModal = false
      await this.LogOut()
      setTimeout(() => this.$router.push({ name: 'Login' }), 1000)
    },
    setAvatarText() {
      let fullname = this.givenNameAndSurname.split(' ')
      const sizeName = fullname.length

      let firstName = fullname[0].charAt(0)
      let lastName = fullname[sizeName - 1].charAt(0)

      this.avatarText = firstName + lastName
    },
  },
}
</script>

<style scoped>
.reset-line-height {
  line-height: normal;
}
</style>
