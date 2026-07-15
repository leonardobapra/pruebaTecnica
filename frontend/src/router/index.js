import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../views/MainLayout.vue'
import PatientsView from '../views/PatientsView.vue'
import DashboardView from '../views/DashboardView.vue'
import LoginView from '../views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'patients',
          component: PatientsView,
        },
        {
          path: 'dashboard-page',
          name: 'dashboard',
          component: DashboardView,
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
  ],
})

// Authentication Navigation Guard
router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('token')

  if (to.name !== 'login' && !isAuthenticated) {
    next({ name: 'login' })
  } else if (to.name === 'login' && isAuthenticated) {
    next({ name: 'patients' })
  } else {
    next()
  }
})

export default router
