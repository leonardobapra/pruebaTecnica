<template>
  <v-table theme="dark" class="patients-table border rounded-lg">
    <thead>
      <tr>
        <th class="text-left font-weight-bold text-grey-lighten-1">Documento</th>
        <th class="text-left font-weight-bold text-grey-lighten-1">Nombre Completo</th>
        <th class="text-left font-weight-bold text-grey-lighten-1">Edad</th>
        <th class="text-left font-weight-bold text-grey-lighten-1">Género</th>
        <th class="text-left font-weight-bold text-grey-lighten-1">Teléfono</th>
        <th class="text-left font-weight-bold text-grey-lighten-1">EPS</th>
        <th class="text-left font-weight-bold text-grey-lighten-1">Prioridad</th>
        <th class="text-left font-weight-bold text-grey-lighten-1">Estado</th>
        <th class="text-center font-weight-bold text-grey-lighten-1">Acciones</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="patient in patients" :key="patient.id" class="table-row">
        <td>
          <div class="d-flex align-center">
            <v-chip size="x-small" variant="tonal" color="cyan" class="mr-2 font-weight-bold">
              {{ patient.documentType }}
            </v-chip>
            <span class="font-weight-medium text-body-2">{{ patient.documentNumber }}</span>
          </div>
        </td>
        <td class="font-weight-bold text-body-2 text-white">{{ patient.fullName }}</td>
        <td>{{ calculateAge(patient.birthDate) }} años</td>
        <td>{{ patient.gender }}</td>
        <td>{{ patient.phone }}</td>
        <td>
          <div class="d-flex flex-column">
            <span class="text-caption text-grey font-weight-bold">{{ patient.epsCode }}</span>
            <span class="text-body-2 text-grey-lighten-2">{{ patient.epsName }}</span>
          </div>
        </td>
        <td>
          <v-chip
            :color="getPriorityColor(patient.priority)"
            size="small"
            variant="flat"
            class="font-weight-bold"
          >
            {{ patient.priority }}
          </v-chip>
        </td>
        <td>
          <v-chip
            :color="getStatusColor(patient.status)"
            size="small"
            variant="outlined"
            class="font-weight-bold"
          >
            {{ patient.status }}
          </v-chip>
        </td>
        <td class="text-center">
          <div class="d-flex justify-center align-center ga-1">
            <v-btn
              icon="mdi-pencil"
              variant="text"
              color="cyan"
              size="small"
              title="Editar"
              @click="$emit('edit', patient)"
            ></v-btn>
            <v-btn
              icon="mdi-delete"
              variant="text"
              color="red-lighten-1"
              size="small"
              title="Eliminar"
              @click="$emit('delete', patient.id)"
            ></v-btn>
          </div>
        </td>
      </tr>
      <tr v-if="patients.length === 0">
        <td colspan="9" class="text-center py-8 text-grey italic">
          No hay pacientes registrados en este momento o que coincidan con la búsqueda.
        </td>
      </tr>
    </tbody>
  </v-table>
</template>

<script>
export default {
  name: 'PatientTable',
  props: {
    patients: {
      type: Array,
      required: true
    }
  },
  methods: {
    calculateAge(birthDateStr) {
      if (!birthDateStr) return ''
      const birthDate = new Date(birthDateStr)
      const today = new Date()
      let age = today.getFullYear() - birthDate.getFullYear()
      const m = today.getMonth() - birthDate.getMonth()
      if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--
      }
      return age
    },
    getPriorityColor(priority) {
      if (!priority) return 'default'
      const p = priority.toLowerCase()
      if (p === 'alta') return 'rose-accent-2' // error or red
      if (p === 'media') return 'amber-darken-1' // warning
      return 'blue-grey' // baja
    },
    getStatusColor(status) {
      if (!status) return 'default'
      const s = status.toLowerCase()
      if (s === 'pendiente') return 'grey-lighten-1'
      if (s === 'en atención' || s === 'en atencion') return 'cyan'
      return 'emerald' // success
    }
  }
}
</script>

<style scoped>
.patients-table {
  background: rgba(255, 255, 255, 0.02) !important;
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* Force horizontal scroll on mobile for table readability */
.patients-table :deep(table) {
  min-width: 900px;
}

.table-row {
  transition: background-color 0.2s ease;
}

.table-row:hover {
  background-color: rgba(255, 255, 255, 0.015) !important;
}

/* Custom CSS for emerald status color */
:deep(.v-chip.text-emerald) {
  color: #10b981 !important;
  border-color: rgba(16, 185, 129, 0.4) !important;
}
:deep(.v-chip.bg-rose-accent-2) {
  background-color: #f43f5e !important;
  color: #ffffff !important;
}
</style>
