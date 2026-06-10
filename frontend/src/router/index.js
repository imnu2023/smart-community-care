import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    redirect: '/dashboard/health',
    children: [
      {
        path: 'health',
        name: 'Health',
        component: () => import('../views/Health.vue')
      },
      {
        path: 'health-input',
        name: 'HealthInput',
        component: () => import('../views/HealthInput.vue')
      },
      {
        path: 'devices',
        name: 'Devices',
        component: () => import('../views/Devices.vue')
      },
      {
        path: 'emergency',
        name: 'Emergency',
        component: () => import('../views/Emergency.vue')
      },
      {
        path: 'services',
        name: 'Services',
        component: () => import('../views/Services.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/AdminDashboard.vue'),
    children: [
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue')
      },
      {
        path: 'devices',
        name: 'AdminDevices',
        component: () => import('../views/admin/AdminDevices.vue')
      },
      {
        path: 'emergencies',
        name: 'AdminEmergencies',
        component: () => import('../views/admin/Emergencies.vue')
      },
      {
        path: 'services',
        name: 'AdminServices',
        component: () => import('../views/admin/Services.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('../views/admin/AdminOrders.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router