<template>
  <v-container class="py-8" max-width="1200px">
    <!-- Header Section -->
    <v-card class="mb-6 glass-card header-border" variant="outlined">
      <v-card-text class="d-flex justify-space-between align-center flex-wrap ga-4">
        <div>
          <h1 class="text-h4 font-weight-bold text-white mb-1">
            <v-icon icon="mdi-view-dashboard" class="mr-2 text-cyan" size="36"></v-icon>
            Dashboard Operativo
          </h1>
          <p class="text-subtitle-2 text-grey-lighten-1">
            Métricas de la sala de espera y distribución de pacientes en tiempo real
          </p>
        </div>
        <div>
          <v-btn
            color="cyan"
            variant="outlined"
            prepend-icon="mdi-refresh"
            class="text-none font-weight-bold"
            @click="loadData"
            :loading="loading"
          >
            Actualizar Métricas
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- Loading State -->
    <div v-if="loading && !dataLoaded" class="d-flex flex-column align-center justify-center py-12 text-center">
      <v-progress-circular indeterminate color="cyan" size="50" class="mb-4"></v-progress-circular>
      <span class="text-body-1 text-grey-lighten-1">Cargando indicadores...</span>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="d-flex flex-column align-center justify-center py-12 text-center px-4">
      <v-icon icon="mdi-alert-circle" color="red-accent-2" size="48" class="mb-3"></v-icon>
      <p class="text-body-1 text-red-accent-1 font-weight-medium mb-4">{{ error }}</p>
      <v-btn color="cyan" class="text-none font-weight-bold text-white" @click="loadData">
        Reintentar
      </v-btn>
    </div>

    <!-- Dashboard Content -->
    <div v-else>
      <!-- KPI Grid -->
      <v-row class="mb-6">
        <!-- Total Registered -->
        <v-col cols="12" sm="6" md="4" lg="2.4" class="py-2">
          <v-card class="glass-card kpi-card border-indigo" variant="outlined">
            <v-card-text class="d-flex flex-column pa-5">
              <div class="d-flex justify-space-between align-center mb-4">
                <span class="text-caption font-weight-bold text-grey-lighten-1 text-uppercase">Total</span>
                <v-icon icon="mdi-account-multiple" color="indigo-lighten-2" size="24"></v-icon>
              </div>
              <span class="text-h4 font-weight-bold text-white mb-1">{{ stats.total }}</span>
              <span class="text-caption text-grey">Pacientes registrados</span>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Pending -->
        <v-col cols="12" sm="6" md="4" lg="2.4" class="py-2">
          <v-card class="glass-card kpi-card border-amber" variant="outlined">
            <v-card-text class="d-flex flex-column pa-5">
              <div class="d-flex justify-space-between align-center mb-4">
                <span class="text-caption font-weight-bold text-grey-lighten-1 text-uppercase">Pendientes</span>
                <v-icon icon="mdi-clock-outline" color="amber-darken-1" size="24"></v-icon>
              </div>
              <span class="text-h4 font-weight-bold text-white mb-1">{{ stats.pendientes }}</span>
              <span class="text-caption text-grey">En sala de espera</span>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- In Care -->
        <v-col cols="12" sm="6" md="4" lg="2.4" class="py-2">
          <v-card class="glass-card kpi-card border-cyan" variant="outlined">
            <v-card-text class="d-flex flex-column pa-5">
              <div class="d-flex justify-space-between align-center mb-4">
                <span class="text-caption font-weight-bold text-grey-lighten-1 text-uppercase">En Atención</span>
                <v-icon icon="mdi-account-heart" color="cyan-lighten-1" size="24"></v-icon>
              </div>
              <span class="text-h4 font-weight-bold text-white mb-1">{{ stats.enAtencion }}</span>
              <span class="text-caption text-grey">Actualmente con médico</span>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Attended -->
        <v-col cols="12" sm="6" md="4" lg="2.4" class="py-2">
          <v-card class="glass-card kpi-card border-emerald" variant="outlined">
            <v-card-text class="d-flex flex-column pa-5">
              <div class="d-flex justify-space-between align-center mb-4">
                <span class="text-caption font-weight-bold text-grey-lighten-1 text-uppercase">Atendidos</span>
                <v-icon icon="mdi-check-circle-outline" color="emerald-lighten-1" size="24"></v-icon>
              </div>
              <span class="text-h4 font-weight-bold text-white mb-1">{{ stats.atendidos }}</span>
              <span class="text-caption text-grey">Servicio completado</span>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- High Priority -->
        <v-col cols="12" sm="6" md="4" lg="2.4" class="py-2">
          <v-card class="glass-card kpi-card border-rose" variant="outlined">
            <v-card-text class="d-flex flex-column pa-5">
              <div class="d-flex justify-space-between align-center mb-4">
                <span class="text-caption font-weight-bold text-grey-lighten-1 text-uppercase">Prioridad Alta</span>
                <v-icon icon="mdi-alert-decagram" color="rose-lighten-1" size="24"></v-icon>
              </div>
              <span class="text-h4 font-weight-bold text-white mb-1">{{ stats.prioridadAlta }}</span>
              <span class="text-caption text-grey">Requieren atención urgente</span>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Graphical Indicators and Case Distribution -->
      <v-row class="mb-6">
        <!-- Progress Circular / Efficiency -->
        <v-col cols="12" md="6" class="py-2">
          <v-card class="glass-card h-100" variant="outlined">
            <v-card-title class="text-h6 font-weight-bold text-white pa-5 pb-2">
              Eficiencia de Atención
            </v-card-title>
            <v-card-text class="d-flex flex-column align-center justify-center pa-6">
              <v-progress-circular
                :model-value="efficiencyRate"
                :size="160"
                :width="15"
                color="emerald-accent-3"
                class="mb-4"
              >
                <div class="d-flex flex-column align-center">
                  <span class="text-h4 font-weight-bold text-white">{{ Math.round(efficiencyRate) }}%</span>
                  <span class="text-caption text-grey">Atendidos</span>
                </div>
              </v-progress-circular>
              <div class="text-center max-width-text mt-2">
                <span class="text-body-2 text-grey-lighten-1">
                  Se ha completado la atención médica para <strong>{{ stats.atendidos }}</strong> de los <strong>{{ stats.total }}</strong> pacientes registrados en la lista de espera.
                </span>
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Status Distribution Progress Bars -->
        <v-col cols="12" md="6" class="py-2">
          <v-card class="glass-card h-100" variant="outlined">
            <v-card-title class="text-h6 font-weight-bold text-white pa-5 pb-2">
              Distribución de Sala de Espera
            </v-card-title>
            <v-card-text class="pa-6">
              <!-- Pending bar -->
              <div class="mb-4">
                <div class="d-flex justify-space-between text-body-2 mb-1">
                  <span class="text-grey-lighten-2">Pendientes en Sala</span>
                  <span class="font-weight-bold text-amber">{{ stats.pendientes }} ({{ getPercentage(stats.pendientes) }}%)</span>
                </div>
                <v-progress-linear
                  :model-value="getPercentage(stats.pendientes)"
                  color="amber"
                  height="8"
                  rounded
                ></v-progress-linear>
              </div>

              <!-- In Care bar -->
              <div class="mb-4">
                <div class="d-flex justify-space-between text-body-2 mb-1">
                  <span class="text-grey-lighten-2">En Atención Médica</span>
                  <span class="font-weight-bold text-cyan">{{ stats.enAtencion }} ({{ getPercentage(stats.enAtencion) }}%)</span>
                </div>
                <v-progress-linear
                  :model-value="getPercentage(stats.enAtencion)"
                  color="cyan"
                  height="8"
                  rounded
                ></v-progress-linear>
              </div>

              <!-- Attended bar -->
              <div class="mb-4">
                <div class="d-flex justify-space-between text-body-2 mb-1">
                  <span class="text-grey-lighten-2">Atendidos / Egresados</span>
                  <span class="font-weight-bold text-emerald">{{ stats.atendidos }} ({{ getPercentage(stats.atendidos) }}%)</span>
                </div>
                <v-progress-linear
                  :model-value="getPercentage(stats.atendidos)"
                  color="emerald"
                  height="8"
                  rounded
                ></v-progress-linear>
              </div>

              <!-- Priority ratio -->
              <div class="mt-6 pa-3 rounded-lg border info-box">
                <div class="d-flex align-center text-body-2">
                  <v-icon icon="mdi-alert-decagram" color="rose-lighten-1" class="mr-2"></v-icon>
                  <span class="text-grey-lighten-2">
                    El <strong>{{ getPercentage(stats.prioridadAlta) }}%</strong> de los pacientes actualmente en espera requiere de atención con <strong>prioridad alta</strong>.
                  </span>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Recent High Priority Cases -->
      <v-card class="glass-card" variant="outlined">
        <v-card-title class="text-h6 font-weight-bold text-white pa-5 pb-2">
          Casos Recientes de Prioridad Alta
        </v-card-title>
        <v-card-text class="pa-0">
          <v-table theme="dark" class="bg-transparent border-0 pa-4">
            <thead>
              <tr>
                <th class="text-left text-grey">Paciente</th>
                <th class="text-left text-grey">Documento</th>
                <th class="text-left text-grey">EPS</th>
                <th class="text-left text-grey">Estado</th>
                <th class="text-left text-grey">Ingreso</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="caseItem in recentPriorityCases" :key="caseItem.id">
                <td class="font-weight-bold text-white">{{ caseItem.fullName }}</td>
                <td>
                  <v-chip size="x-small" color="cyan" variant="tonal" class="mr-2 font-weight-bold">
                    {{ caseItem.documentType }}
                  </v-chip>
                  <span>{{ caseItem.documentNumber }}</span>
                </td>
                <td>
                  <span class="text-caption text-grey font-weight-bold mr-2">{{ caseItem.epsCode }}</span>
                  <span>{{ caseItem.epsName }}</span>
                </td>
                <td>
                  <v-chip size="small" color="cyan" variant="outlined" class="font-weight-bold">
                    {{ caseItem.status }}
                  </v-chip>
                </td>
                <td class="text-caption text-grey">{{ formatTime(caseItem.createdAt) }}</td>
              </tr>
              <tr v-if="recentPriorityCases.length === 0">
                <td colspan="5" class="text-center py-6 text-grey italic">
                  No hay casos pendientes de prioridad alta registrados.
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
      </v-card>
    </div>
  </v-container>
</template>

<script>
import patientsApi from '@/api/patients'

export default {
  name: 'DashboardView',
  data() {
    return {
      stats: {
        total: 0,
        pendientes: 0,
        enAtencion: 0,
        atendidos: 0,
        prioridadAlta: 0
      },
      recentPriorityCases: [],
      loading: false,
      dataLoaded: false,
      error: null
    }
  },
  computed: {
    efficiencyRate() {
      if (this.stats.total === 0) return 0
      return (this.stats.atendidos / this.stats.total) * 100
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      this.error = null
      try {
        // Fetch dashboard statistics
        const statsResponse = await patientsApi.getDashboard()
        this.stats = statsResponse.data || {
          total: 0,
          pendientes: 0,
          enAtencion: 0,
          atendidos: 0,
          prioridadAlta: 0
        }

        // Fetch top 5 recent high priority cases
        const casesResponse = await patientsApi.getPatients({
          priority: 'Alta',
          page: 0,
          size: 5,
          sort: 'id,desc'
        })
        this.recentPriorityCases = casesResponse.data.content || []
        
        this.dataLoaded = true
      } catch (err) {
        console.error(err)
        this.error = 'Error al conectar con el servidor para cargar las estadísticas.'
      } finally {
        this.loading = false
      }
    },
    getPercentage(value) {
      if (this.stats.total === 0) return 0
      return ((value / this.stats.total) * 100).toFixed(1)
    },
    formatTime(dateTimeStr) {
      if (!dateTimeStr) return ''
      try {
        const dt = new Date(dateTimeStr)
        return dt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' - ' + dt.toLocaleDateString()
      } catch (e) {
        return dateTimeStr
      }
    }
  }
}
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.02) !important;
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  border-radius: 16px !important;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3) !important;
}

.header-border {
  border-left: 4px solid var(--accent-cyan) !important;
}

.stat-badge {
  background: rgba(255, 255, 255, 0.04) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.border-top {
  border-top: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.info-box {
  background: rgba(255, 255, 255, 0.01) !important;
  border-color: rgba(255, 255, 255, 0.05) !important;
}

.max-width-text {
  max-width: 320px;
}

/* KPI Cards glowing borders */
.kpi-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.kpi-card:hover {
  transform: translateY(-2px);
}

.border-indigo {
  border-left: 4px solid #8b5cf6 !important;
}
.border-indigo:hover {
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.15) !important;
}

.border-amber {
  border-left: 4px solid #f59e0b !important;
}
.border-amber:hover {
  box-shadow: 0 0 15px rgba(245, 158, 11, 0.15) !important;
}

.border-cyan {
  border-left: 4px solid #06b6d4 !important;
}
.border-cyan:hover {
  box-shadow: 0 0 15px rgba(6, 182, 212, 0.15) !important;
}

.border-emerald {
  border-left: 4px solid #10b981 !important;
}
.border-emerald:hover {
  box-shadow: 0 0 15px rgba(16, 185, 129, 0.15) !important;
}

.border-rose {
  border-left: 4px solid #f43f5e !important;
}
.border-rose:hover {
  box-shadow: 0 0 15px rgba(244, 63, 94, 0.15) !important;
}

/* SVG Customizations */
:deep(.v-progress-circular__underlay) {
  stroke: rgba(255, 255, 255, 0.03) !important;
}
</style>
