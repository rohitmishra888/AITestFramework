import { useState } from 'react'
import { motion } from 'framer-motion'
import { 
  ChevronDown, 
  ChevronRight, 
  CheckCircle, 
  Clock, 
  Calendar,
  AlertCircle,
  FileText,
  Target,
  Shield,
  Lightbulb,
  TestTube,
  GitBranch
} from 'lucide-react'
import { AnalysisResponse } from '../lib/api'

interface AnalysisResultsProps {
  result: AnalysisResponse
}

export function AnalysisResults({ result }: AnalysisResultsProps) {
  const [expandedSections, setExpandedSections] = useState<Set<string>>(new Set(['summary']))

  const toggleSection = (section: string) => {
    const newExpanded = new Set(expandedSections)
    if (newExpanded.has(section)) {
      newExpanded.delete(section)
    } else {
      newExpanded.add(section)
    }
    setExpandedSections(newExpanded)
  }

  const getSeverityColor = (severity: string) => {
    switch (severity?.toLowerCase()) {
      case 'high':
        return 'text-red-600 bg-red-100 dark:text-red-400 dark:bg-red-900/20'
      case 'medium':
        return 'text-yellow-600 bg-yellow-100 dark:text-yellow-400 dark:bg-yellow-900/20'
      case 'low':
        return 'text-green-600 bg-green-100 dark:text-green-400 dark:bg-green-900/20'
      default:
        return 'text-gray-600 bg-gray-100 dark:text-gray-400 dark:bg-gray-900/20'
    }
  }

  const getRiskLevelColor = (riskLevel: string) => {
    switch (riskLevel?.toLowerCase()) {
      case 'high':
        return 'text-red-600 bg-red-100 dark:text-red-400 dark:bg-red-900/20'
      case 'medium':
        return 'text-yellow-600 bg-yellow-100 dark:text-yellow-400 dark:bg-yellow-900/20'
      case 'low':
        return 'text-green-600 bg-green-100 dark:text-green-400 dark:bg-green-900/20'
      default:
        return 'text-gray-600 bg-gray-100 dark:text-gray-400 dark:bg-gray-900/20'
    }
  }

  const getStatusColor = (status: string) => {
    switch (status?.toLowerCase()) {
      case 'to do':
        return 'text-gray-600 bg-gray-100 dark:text-gray-400 dark:bg-gray-900/20'
      case 'in progress':
        return 'text-blue-600 bg-blue-100 dark:text-blue-400 dark:bg-blue-900/20'
      case 'done':
        return 'text-green-600 bg-green-100 dark:text-green-400 dark:bg-green-900/20'
      default:
        return 'text-gray-600 bg-gray-100 dark:text-gray-400 dark:bg-gray-900/20'
    }
  }

  return (
    <div className="space-y-6">
      {/* Analysis Summary */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="card"
      >
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-2xl font-bold text-gray-900 dark:text-white">
            Analysis Summary
          </h3>
          <div className="flex items-center space-x-6 text-sm text-gray-600 dark:text-gray-300">
            <div className="flex items-center space-x-2">
              <Clock className="h-5 w-5 text-blue-500" />
              <span className="font-medium">{result.metadata?.processingTime || 0}ms</span>
            </div>
            <div className="flex items-center space-x-2">
              <CheckCircle className="h-5 w-5 text-green-500" />
              <span className="font-medium">{result.metadata?.ticketsAnalyzed || 0} tickets analyzed</span>
            </div>
            <div className="flex items-center space-x-2">
              <TestTube className="h-5 w-5 text-purple-500" />
              <span className="font-medium">Model: {result.metadata?.modelUsed || 'N/A'}</span>
            </div>
          </div>
        </div>
        
        <div className="bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 rounded-lg p-6 border border-blue-200 dark:border-blue-800">
          <p className="text-gray-700 dark:text-gray-300 text-lg leading-relaxed">
            {result.report?.summary || 'Analysis completed successfully.'}
          </p>
        </div>
      </motion.div>

      {/* Gaps Identified */}
      {result.report?.gapsIdentified && result.report.gapsIdentified.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="card"
        >
          <button
            onClick={() => toggleSection('gaps')}
            className="flex items-center justify-between w-full text-left mb-4"
          >
            <div className="flex items-center space-x-2">
              <AlertCircle className="h-5 w-5 text-orange-500" />
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
                Gaps Identified ({result.report.gapsIdentified.length})
              </h3>
            </div>
            {expandedSections.has('gaps') ? (
              <ChevronDown className="h-5 w-5 text-gray-500" />
            ) : (
              <ChevronRight className="h-5 w-5 text-gray-500" />
            )}
          </button>
          
          {expandedSections.has('gaps') && (
            <div className="space-y-4">
              {result.report.gapsIdentified.map((gap: any, index: number) => (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="border border-orange-200 dark:border-orange-800 rounded-lg p-4 bg-orange-50 dark:bg-orange-900/10"
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex items-center space-x-2">
                      <span className={`px-3 py-1 text-sm font-semibold rounded-full ${getSeverityColor(gap.severity)}`}>
                        {gap.severity}
                      </span>
                      <span className="text-sm font-medium text-gray-600 dark:text-gray-400">
                        {gap.category}
                      </span>
                    </div>
                  </div>
                  
                  <h4 className="font-medium text-gray-900 dark:text-white mb-2">
                    {gap.description}
                  </h4>
                  
                  <div className="mb-3">
                    <h5 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Impact:</h5>
                    <p className="text-sm text-gray-600 dark:text-gray-400">{gap.impact}</p>
                  </div>
                  
                  {gap.suggestions && gap.suggestions.length > 0 && (
                    <div>
                      <h5 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Suggestions:</h5>
                      <ul className="space-y-1">
                        {gap.suggestions.map((suggestion: string, idx: number) => (
                          <li key={idx} className="flex items-start space-x-2 text-sm text-gray-600 dark:text-gray-400">
                            <span className="flex-shrink-0 w-1.5 h-1.5 bg-orange-400 rounded-full mt-2"></span>
                            <span>{suggestion}</span>
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                </motion.div>
              ))}
            </div>
          )}
        </motion.div>
      )}

      {/* Related Tickets */}
      {result.report?.relatedTickets && result.report.relatedTickets.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="card"
        >
          <button
            onClick={() => toggleSection('relatedTickets')}
            className="flex items-center justify-between w-full text-left mb-4"
          >
            <div className="flex items-center space-x-2">
              <GitBranch className="h-5 w-5 text-blue-500" />
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
                Related Tickets ({result.report.relatedTickets.length})
              </h3>
            </div>
            {expandedSections.has('relatedTickets') ? (
              <ChevronDown className="h-5 w-5 text-gray-500" />
            ) : (
              <ChevronRight className="h-5 w-5 text-gray-500" />
            )}
          </button>
          
          {expandedSections.has('relatedTickets') && (
            <div className="space-y-4">
              {result.report.relatedTickets.map((ticket: any, index: number) => (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="border border-gray-200 dark:border-gray-700 rounded-lg p-4 hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center space-x-2 mb-2">
                        <span className="font-medium text-blue-600 dark:text-blue-400">
                          {ticket.ticketKey}
                        </span>
                        <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(ticket.status)}`}>
                          {ticket.status}
                        </span>
                        <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getSeverityColor(ticket.priority)}`}>
                          {ticket.priority}
                        </span>
                        {ticket.relevanceScore && (
                          <span className="text-sm text-gray-500 dark:text-gray-400">
                            Relevance: {(ticket.relevanceScore * 100).toFixed(1)}%
                          </span>
                        )}
                      </div>
                      <h4 className="font-medium text-gray-900 dark:text-white mb-1">
                        {ticket.summary}
                      </h4>
                      <p className="text-sm text-gray-600 dark:text-gray-300">
                        {ticket.impactDescription}
                      </p>
                      <div className="mt-2">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Relationship: {ticket.relationshipType}
                        </span>
                      </div>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </motion.div>
      )}

      {/* Regression Areas */}
      {result.report?.regressionAreas && result.report.regressionAreas.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="card"
        >
          <button
            onClick={() => toggleSection('regressionAreas')}
            className="flex items-center justify-between w-full text-left mb-4"
          >
            <div className="flex items-center space-x-2">
              <Shield className="h-5 w-5 text-red-500" />
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
                Regression Areas ({result.report.regressionAreas.length})
              </h3>
            </div>
            {expandedSections.has('regressionAreas') ? (
              <ChevronDown className="h-5 w-5 text-gray-500" />
            ) : (
              <ChevronRight className="h-5 w-5 text-gray-500" />
            )}
          </button>
          
          {expandedSections.has('regressionAreas') && (
            <div className="space-y-4">
              {result.report.regressionAreas.map((area: any, index: number) => (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="border border-red-200 dark:border-red-800 rounded-lg p-4 bg-red-50 dark:bg-red-900/10"
                >
                  <div className="flex items-start justify-between mb-3">
                    <h4 className="font-medium text-gray-900 dark:text-white">
                      {area.area}
                    </h4>
                    <span className={`px-3 py-1 text-sm font-semibold rounded-full ${getRiskLevelColor(area.riskLevel)}`}>
                      {area.riskLevel}
                    </span>
                  </div>
                  
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                    {area.description}
                  </p>
                  
                  <div className="mb-3">
                    <h5 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Test Cases:</h5>
                    <ul className="space-y-1">
                      {area.testCases.map((testCase: string, idx: number) => (
                        <li key={idx} className="flex items-start space-x-2 text-sm text-gray-600 dark:text-gray-400">
                          <span className="flex-shrink-0 w-1.5 h-1.5 bg-red-400 rounded-full mt-2"></span>
                          <span>{testCase}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                  
                  <div className="bg-white dark:bg-gray-800 rounded-lg p-3 border border-red-200 dark:border-red-800">
                    <h5 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Rationale:</h5>
                    <p className="text-sm text-gray-600 dark:text-gray-400">{area.rationale}</p>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </motion.div>
      )}

      {/* Recommendations */}
      {result.report?.recommendations && result.report.recommendations.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="card"
        >
          <div className="flex items-center space-x-2 mb-4">
            <Lightbulb className="h-5 w-5 text-yellow-500" />
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
              Recommendations ({result.report.recommendations.length})
            </h3>
          </div>
          <div className="space-y-4">
            {result.report.recommendations.map((rec: any, index: number) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.1 }}
                className="flex items-start space-x-3 p-4 bg-yellow-50 dark:bg-yellow-900/10 rounded-lg border border-yellow-200 dark:border-yellow-800"
              >
                <div className="flex-shrink-0 w-6 h-6 bg-yellow-100 dark:bg-yellow-900/30 rounded-full flex items-center justify-center">
                  <span className="text-sm font-medium text-yellow-600 dark:text-yellow-400">
                    {index + 1}
                  </span>
                </div>
                <div className="flex-1">
                  <p className="text-sm text-gray-700 dark:text-gray-300">
                    {typeof rec === 'string' ? rec : rec.description || rec.title}
                  </p>
                </div>
              </motion.div>
            ))}
          </div>
        </motion.div>
      )}

      {/* Analysis Metadata */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.5 }}
        className="card"
      >
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
          Analysis Details
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
          <div className="flex items-center space-x-2">
            <Clock className="h-4 w-4 text-gray-400" />
            <span className="text-gray-600 dark:text-gray-400">Processing Time:</span>
            <span className="font-medium">{result.metadata?.processingTime || 0}ms</span>
          </div>
          <div className="flex items-center space-x-2">
            <FileText className="h-4 w-4 text-gray-400" />
            <span className="text-gray-600 dark:text-gray-400">Tickets Analyzed:</span>
            <span className="font-medium">{result.metadata?.ticketsAnalyzed || 0}</span>
          </div>
          <div className="flex items-center space-x-2">
            <Target className="h-4 w-4 text-gray-400" />
            <span className="text-gray-600 dark:text-gray-400">Model Used:</span>
            <span className="font-medium">{result.metadata?.modelUsed || 'N/A'}</span>
          </div>
          <div className="flex items-center space-x-2">
            <CheckCircle className="h-4 w-4 text-gray-400" />
            <span className="text-gray-600 dark:text-gray-400">Cache Hit:</span>
            <span className="font-medium">{result.metadata?.cacheHit ? 'Yes' : 'No'}</span>
          </div>
          {result.metadata?.completedAt && (
            <div className="flex items-center space-x-2 md:col-span-2">
              <Calendar className="h-4 w-4 text-gray-400" />
              <span className="text-gray-600 dark:text-gray-400">Completed At:</span>
              <span className="font-medium">
                {new Date(result.metadata.completedAt).toLocaleString()}
              </span>
            </div>
          )}
        </div>
      </motion.div>
    </div>
  )
} 