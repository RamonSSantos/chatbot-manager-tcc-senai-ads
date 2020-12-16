<template>
  <v-card>
    <v-card-title>Perfis de Acesso</v-card-title>
    <v-card-text>
      <v-expansion-panels accordion focusable>
        <v-expansion-panel v-for="item in items" :key="item.id">
          <v-expansion-panel-header class="text-uppercase">{{
            item.title
          }}</v-expansion-panel-header>
          <v-expansion-panel-content>
            <v-list-item v-for="privilege in item.privileges" :key="privilege">
              <v-list-item-icon>
                <v-icon text>mdi-chevron-right</v-icon>
              </v-list-item-icon>
              <v-list-item-content>
                <v-list-item-title>{{ privilege }}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  name: 'Profiles',
  data: () => ({
    items: [
      {
        id: 1,
        title: 'Administrador',
        privileges: [
          'Administra os Usuários',
          'Administra os Perfis de Acesso',
          'Administra os Setores',
          'Administra os Temas',
          'Administra os Conteúdos/Materiais',
          'Monitoramento de Eventos',
          'Acompanhamento de Usuários',
        ],
      },
      {
        id: 2,
        title: 'Especialista',
        privileges: [
          'Administra os Temas',
          'Administra os Conteúdos/Materiais',
          'Monitoramento de Eventos',
          'Acompanhamento de Usuários',
        ],
      },
      {
        id: 3,
        title: 'Colaborador',
        privileges: ['Acesso ao chatbot'],
      },
    ],
  }),
}
</script>
