export default [
  {
    name: 'Events',
    path: '/eventos',
    component: () => import('./Events'),
    meta: {
      title: 'Eventos',
      breadcrumb: 'Eventos',
    },
    children: [
      {
        name: 'EventView',
        path: '/eventos/visualizar/:id',
        component: () => import('./EventView'),
        meta: {
          title: 'Visualizar Evento',
          breadcrumb: 'Visualizar',
        },
        props: true,
      },
    ],
  },
]
