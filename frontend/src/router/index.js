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
    component: () => import('../views/Dashboard.vue')
  },
  {
    path: '/health',
    name: 'Health',
    component: () => import('../views/Health.vue')
  },
  {
    path: '/health-input',
    name: 'HealthInput',
    component: () => import('../views/HealthInput.vue')
  },
  {
    path: '/devices',
    name: 'Devices',
    component: () => import('../views/Devices.vue')
  },
  {
    path: '/emergency',
    name: 'Emergency',
    component: () => import('../views/Emergency.vue')
  },
  {
    path: '/services',
    name: 'Services',
    component: () => import('../views/Services.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/Orders.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue')
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('../views/Messages.vue')
  },
  {
    path: '/activities',
    name: 'Activities',
    component: () => import('../views/Activities.vue')
  },
  {
    path: '/elder-messages',
    redirect: '/messages'
  },
  {
    path: '/family-pay',
    name: 'FamilyPay',
    component: () => import('../views/FamilyPay.vue')
  },
  {
    path: '/family',
    component: () => import('../views/family/FamilyLayout.vue'),
    children: [
      {
        path: '',
        name: 'FamilyDashboard',
        component: () => import('../views/family/FamilyDashboard.vue')
      },
      {
        path: 'elder-health',
        name: 'ElderHealth',
        component: () => import('../views/family/ElderHealth.vue')
      },
      {
        path: 'orders',
        name: 'FamilyOrders',
        component: () => import('../views/family/FamilyOrders.vue')
      },
      {
        path: 'messages',
        name: 'FamilyMessages',
        component: () => import('../views/family/FamilyMessages.vue')
      },
      {
        path: 'activities',
        name: 'FamilyActivities',
        component: () => import('../views/family/FamilyActivities.vue')
      },
      {
        path: 'services',
        name: 'FamilyServices',
        component: () => import('../views/family/FamilyServices.vue')
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
      },
      {
        path: 'relations',
        name: 'AdminRelations',
        component: () => import('../views/admin/Relations.vue')
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
    return
  }
  // Redirect family members to their dedicated portal
  const role = parseInt(localStorage.getItem('role'))
  const elderPaths = ['/dashboard', '/health', '/health-input', '/devices', '/emergency', '/services', '/orders', '/activities', '/family-pay']
  if (role === 3 && elderPaths.includes(to.path)) {
    next('/family')
    return
  }
  next()
})

export default router
