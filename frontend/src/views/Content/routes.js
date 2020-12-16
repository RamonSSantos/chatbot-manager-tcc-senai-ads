export default [
  {
    name: 'Contents',
    path: '/conteudos',
    component: () => import('./Contents'),
    meta: {
      title: 'Conteúdos',
      breadcrumb: 'Conteúdos',
    },
    children: [
      {
        name: 'ContentNew',
        path: '/conteudos/novo',
        component: () => import('./ContentNew'),
        meta: {
          title: 'Novo Conteúdo',
          breadcrumb: 'Novo',
        },
      },
      {
        name: 'ContentView',
        path: '/conteudos/visualizar/:id',
        component: () => import('./ContentView'),
        meta: {
          title: 'Visualizar Conteúdo',
          breadcrumb: 'Visualizar',
        },
        props: true,
      },
      {
        name: 'ContentEdit',
        path: '/conteudos/editar/:id',
        component: () => import('./ContentEdit'),
        meta: {
          title: 'Editar Conteúdo',
          breadcrumb: 'Editar',
        },
        props: true,
      },
    ],
  },
]
