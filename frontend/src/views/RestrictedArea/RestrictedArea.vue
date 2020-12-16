<template>
  <v-card class="grey lighten-4 my-4">
    <Breadcrumb />
    <v-card-title class="text-uppercase grey--text text--darken-1 pt-0 pl-6">
      Área Restrita
    </v-card-title>
    <v-tabs
      v-model="tab"
      background-color="white"
      color="orange accent-4"
      vertical
    >
      <v-tab v-for="tab in tabs" :key="tab.id" :to="{ name: tab.name }">
        {{ tab.title }}
      </v-tab>
      <template v-if="tab === '/web/area-restrita/perfis'">
        <v-tab-item value="/web/area-restrita/perfis">
          <Profiles />
        </v-tab-item>
      </template>
      <template v-else-if="tab === '/web/area-restrita/setores'">
        <v-tab-item value="/web/area-restrita/setores">
          <Sectors />
        </v-tab-item>
      </template>
      <template v-else-if="tab === '/web/area-restrita/temas'">
        <v-tab-item value="/web/area-restrita/temas">
          <Topics />
        </v-tab-item>
      </template>
    </v-tabs>
  </v-card>
</template>

<script>
import Breadcrumb from '@/components/breadcrumb/Breadcrumb'
import Profiles from './Profiles'
import Sectors from './Sectors'
import Topics from './Topics'

export default {
  name: 'RestrictedArea',
  components: {
    Breadcrumb,
    Profiles,
    Sectors,
    Topics,
  },
  data: () => ({
    tab: null,
    tabs: [
      { id: 1, title: 'Perfis', name: 'Profiles' },
      { id: 2, title: 'Setores', name: 'Sectors' },
      { id: 3, title: 'Temas', name: 'Topics' },
    ],
  }),
}
</script>
