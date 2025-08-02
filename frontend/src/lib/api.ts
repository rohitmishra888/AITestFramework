import axios from 'axios'

const API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor for adding auth tokens
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for handling errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('authToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export interface AnalysisRequest {
  ticketId: string
  options: {
    includeComments: boolean
    includeAttachments: boolean
    analysisDepth: 'basic' | 'detailed' | 'comprehensive'
    maxRelatedTickets: number
    minRelevanceScore: number
  }
}

export interface AnalysisResponse {
  analysisId: string
  ticketId: string
  status: string
  report: {
    summary: string
    gapsIdentified?: Array<{
      category: string
      description: string
      severity: 'High' | 'Medium' | 'Low'
      impact: string
      suggestions: string[]
    }>
    relatedTickets?: Array<{
      ticketKey: string
      summary: string
      status: string
      priority: string
      relevanceScore: number
      relationshipType: string
      impactDescription: string
    }>
    regressionAreas?: Array<{
      area: string
      description: string
      riskLevel: 'High' | 'Medium' | 'Low'
      testCases: string[]
      rationale: string
    }>
    recommendations?: string[]
    riskAssessment?: {
      risks: Array<{
        level: string
        title: string
        description: string
        mitigation?: string
      }>
    }
    metrics?: {
      impactScore: number
      affectedTeams: number
      timelineImpact: string
    }
  }
  metadata: {
    processingTime: number
    ticketsAnalyzed: number
    cacheHit: boolean
    completedAt: string
    modelUsed: string
    createdAt?: string
  }
}

export const api = {
  // Analyze a Jira ticket
  analyzeTicket: async (request: AnalysisRequest): Promise<AnalysisResponse> => {
    const response = await apiClient.post('/api/analysis/analyze', request)
    return response.data
  },

  // Get analysis status
  getAnalysisStatus: async (analysisId: string): Promise<AnalysisResponse> => {
    const response = await apiClient.get(`/api/analysis/status/${analysisId}`)
    return response.data
  },

  // Get analysis history
  getAnalysisHistory: async (page: number = 0, size: number = 20): Promise<AnalysisResponse[]> => {
    const response = await apiClient.get('/api/analysis/history', {
      params: { page, size }
    })
    return response.data
  },

  // Delete analysis
  deleteAnalysis: async (analysisId: string): Promise<void> => {
    await apiClient.delete(`/api/analysis/${analysisId}`)
  },

  // Health check
  healthCheck: async (): Promise<string> => {
    const response = await apiClient.get('/api/analysis/health')
    return response.data
  },
} 