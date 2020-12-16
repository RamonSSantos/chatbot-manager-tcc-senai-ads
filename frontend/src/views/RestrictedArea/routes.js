export default [
  {
    name: 'RestrictedArea',
    path: '/area-restrita',
    component: () => import('./RestrictedArea'),
    meta: {
      title: 'Área Restrita',
      breadcrumb: 'Área Restrita',
    },
    children: [
      {
        name: 'Profiles',
        path: '/area-restrita/perfis',
        component: () => import('./Profiles'),
        meta: {
          title: 'Perfis',
          breadcrumb: 'Perfis',
        },
      },
      {
        name: 'Sectors',
        path: '/area-restrita/setores',
        component: () => import('./Sectors'),
        meta: {
          title: 'Setores',
          breadcrumb: 'Setores',
        },
      },
      {
        name: 'Topics',
        path: '/area-restrita/temas',
        component: () => import('./Topics'),
        meta: {
          title: 'Temas',
          breadcrumb: 'Temas',
        },
      },
    ],
  },
]
