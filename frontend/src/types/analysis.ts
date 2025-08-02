export interface AnalysisResult {
  analysisId: string
  status: string
  report: AnalysisReport
  metadata: AnalysisMetadata
}

export interface AnalysisReport {
  summary: string
  gapsIdentified?: Gap[]
  relatedTickets?: RelatedTicket[]
  regressionAreas?: RegressionArea[]
  recommendations?: string[]
}

export interface Gap {
  category: string
  description: string
  severity: 'High' | 'Medium' | 'Low'
  impact: string
  suggestions: string[]
}

export interface RelatedTicket {
  ticketKey: string
  summary: string
  status: string
  priority: string
  relevanceScore: number
  relationshipType: string
  impactDescription: string
}

export interface RegressionArea {
  area: string
  description: string
  riskLevel: 'High' | 'Medium' | 'Low'
  testCases: string[]
  rationale: string
}

export interface AnalysisMetadata {
  processingTime: number
  ticketsAnalyzed: number
  cacheHit: boolean
  completedAt: string
  modelUsed: string
} 