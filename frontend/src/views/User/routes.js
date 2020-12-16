export default [
  {
    name: 'Users',
    path: '/usuarios',
    component: () => import('./Users'),
    meta: {
      title: 'Usuários',
      breadcrumb: 'Usuários',
    },
    children: [
      {
        name: 'UserNew',
        path: '/usuarios/novo',
        component: () => import('./UserNew'),
        meta: {
          title: 'Novo Usuário',
          breadcrumb: 'Novo',
        },
      },
      {
        name: 'UserView',
        path: '/usuarios/visualizar/:id',
        component: () => import('./UserView'),
        meta: {
          title: 'Visualizar Usuário',
          breadcrumb: 'Visualizar',
        },
        props: true,
      },
      {
        name: 'UserEdit',
        path: '/usuarios/editar/:id',
        component: () => import('./UserEdit'),
        meta: {
          title: 'Editar Usuário',
          breadcrumb: 'Editar',
        },
        props: true,
      },
    ],
  },
]
