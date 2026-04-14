import { http } from '@/shared/api/http'

// Utilisateur tel que renvoyé par le backend d'administration.
export type AdminUserResponse = {
  id: string
  username: string
  nomComplet: string
  actif: boolean
  roles: string[]
}

// Rôle tel que renvoyé par le backend.
export type AdminRoleResponse = {
  id: string
  code: string
  libelle: string
}

// Payload de création d'utilisateur.
export type CreateAdminUserRequest = {
  username: string
  password: string
  nomComplet: string
  roles: string[]
}

export async function listAdminUsers() {
  const { data } = await http.get<AdminUserResponse[]>('/api/utilisateurs')
  return data
}

export async function listAdminRoles() {
  const { data } = await http.get<AdminRoleResponse[]>('/api/roles')
  return data
}

export async function createAdminUser(payload: CreateAdminUserRequest) {
  const { data } = await http.post<AdminUserResponse>('/api/utilisateurs', payload)
  return data
}

export async function setAdminUserActivation(id: string, actif: boolean) {
  const { data } = await http.patch<AdminUserResponse>(`/api/utilisateurs/${id}/activation`, { actif })
  return data
}

export async function resetAdminUserPassword(id: string, newPassword: string) {
  const { data } = await http.patch<AdminUserResponse>(`/api/utilisateurs/${id}/password`, { newPassword })
  return data
}

export async function deleteAdminUser(id: string) {
  await http.delete(`/api/utilisateurs/${id}`)
}
