import { motion } from 'framer-motion';
import {
    AlertTriangle,
    CheckCircle,
    Clock,
    Cloud,
    Edit,
    Eye,
    Info,
    MapPin,
    Plus,
    Search,
    Trash2,
    X
} from 'lucide-react';
import { useState } from 'react';
import IntelligenceForm from '../components/IntelligenceForm';
import Modal from '../components/Modal';
import { useTactical } from '../context/TacticalContext';

function Intelligence() {
  const { state, actions } = useTactical();
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedIntel, setSelectedIntel] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [typeFilter, setTypeFilter] = useState('ALL');
  const [severityFilter, setSeverityFilter] = useState('ALL');
  const [verifiedFilter, setVerifiedFilter] = useState('ALL');

  const filteredIntelligence = state.intelligence.filter(intel => {
    const matchesSearch = intel.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         intel.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = typeFilter === 'ALL' || intel.type === typeFilter;
    const matchesSeverity = severityFilter === 'ALL' || intel.severity === severityFilter;
    const matchesVerified = verifiedFilter === 'ALL' || 
                           (verifiedFilter === 'VERIFIED' && intel.verified) ||
                           (verifiedFilter === 'UNVERIFIED' && !intel.verified);
    
    return matchesSearch && matchesType && matchesSeverity && matchesVerified;
  });

  const handleEditIntel = (intel) => {
    setSelectedIntel(intel);
    setShowEditModal(true);
  };

  const handleDeleteIntel = (intelId) => {
    if (window.confirm('Are you sure you want to delete this intelligence report?')) {
      actions.deleteIntelligence(intelId);
    }
  };

  const handleVerifyIntel = (intel) => {
    actions.updateIntelligence({ ...intel, verified: !intel.verified });
  };

  const getTypeIcon = (type) => {
    switch (type) {
      case 'THREAT': return <AlertTriangle className="w-5 h-5" />;
      case 'INFO': return <Info className="w-5 h-5" />;
      case 'ENVIRONMENTAL': return <Cloud className="w-5 h-5" />;
      default: return <Eye className="w-5 h-5" />;
    }
  };

  const getTypeColor = (type) => {
    switch (type) {
      case 'THREAT': return 'text-red-400 bg-red-900/50';
      case 'INFO': return 'text-blue-400 bg-blue-900/50';
      case 'ENVIRONMENTAL': return 'text-yellow-400 bg-yellow-900/50';
      default: return 'text-gray-400 bg-gray-900/50';
    }
  };

  const getSeverityColor = (severity) => {
    switch (severity) {
      case 'HIGH': return 'text-red-400 bg-red-900/50 border-red-500';
      case 'MEDIUM': return 'text-yellow-400 bg-yellow-900/50 border-yellow-500';
      case 'LOW': return 'text-green-400 bg-green-900/50 border-green-500';
      default: return 'text-gray-400 bg-gray-900/50 border-gray-500';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col sm:flex-row justify-between items-start sm:items-center"
      >
        <div>
          <h1 className="text-3xl font-bold text-gray-100">Intelligence Center</h1>
          <p className="text-gray-400 mt-1">Monitor and analyze tactical intelligence</p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="tactical-button-primary flex items-center space-x-2 mt-4 sm:mt-0"
        >
          <Plus className="w-4 h-4" />
          <span>Add Report</span>
        </button>
      </motion.div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="tactical-card p-4"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm">Total Reports</p>
              <p className="text-2xl font-bold text-gray-100">{state.intelligence.length}</p>
            </div>
            <Eye className="w-8 h-8 text-blue-400" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="tactical-card p-4"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm">High Priority</p>
              <p className="text-2xl font-bold text-red-400">
                {state.intelligence.filter(i => i.severity === 'HIGH').length}
              </p>
            </div>
            <AlertTriangle className="w-8 h-8 text-red-400" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="tactical-card p-4"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm">Verified</p>
              <p className="text-2xl font-bold text-green-400">
                {state.intelligence.filter(i => i.verified).length}
              </p>
            </div>
            <CheckCircle className="w-8 h-8 text-green-400" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="tactical-card p-4"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm">Threats</p>
              <p className="text-2xl font-bold text-red-400">
                {state.intelligence.filter(i => i.type === 'THREAT').length}
              </p>
            </div>
            <AlertTriangle className="w-8 h-8 text-red-400" />
          </div>
        </motion.div>
      </div>

      {/* Search and Filters */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="tactical-card p-6"
      >
        <div className="flex flex-col lg:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search intelligence reports..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="tactical-input pl-10 pr-4 py-2 w-full"
            />
          </div>
          
          <select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            className="tactical-input"
          >
            <option value="ALL">All Types</option>
            <option value="THREAT">Threat</option>
            <option value="INFO">Information</option>
            <option value="ENVIRONMENTAL">Environmental</option>
          </select>
          
          <select
            value={severityFilter}
            onChange={(e) => setSeverityFilter(e.target.value)}
            className="tactical-input"
          >
            <option value="ALL">All Severities</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>

          <select
            value={verifiedFilter}
            onChange={(e) => setVerifiedFilter(e.target.value)}
            className="tactical-input"
          >
            <option value="ALL">All Status</option>
            <option value="VERIFIED">Verified</option>
            <option value="UNVERIFIED">Unverified</option>
          </select>
        </div>
      </motion.div>

      {/* Intelligence Reports */}
      <div className="space-y-4">
        {filteredIntelligence.map((intel, index) => (
          <motion.div
            key={intel.id}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.1 }}
            className={`tactical-card p-6 border-l-4 hover:bg-military-700 transition-all duration-300 ${getSeverityColor(intel.severity)}`}
          >
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <div className="flex items-center space-x-3 mb-3">
                  <div className={`p-2 rounded-lg ${getTypeColor(intel.type)}`}>
                    {getTypeIcon(intel.type)}
                  </div>
                  <div>
                    <h3 className="text-lg font-bold text-gray-100">{intel.title}</h3>
                    <div className="flex items-center space-x-4 text-sm text-gray-400">
                      <div className="flex items-center space-x-1">
                        <Clock className="w-4 h-4" />
                        <span>{new Date(intel.timestamp).toLocaleString()}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <MapPin className="w-4 h-4" />
                        <span>
                          {intel.location.lat.toFixed(4)}, {intel.location.lng.toFixed(4)}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <p className="text-gray-300 mb-4">{intel.description}</p>

                <div className="flex items-center space-x-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${getTypeColor(intel.type)}`}>
                    {intel.type}
                  </span>
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${getSeverityColor(intel.severity)}`}>
                    {intel.severity} SEVERITY
                  </span>
                  <span className={`px-3 py-1 rounded-full text-xs font-medium flex items-center space-x-1 ${
                    intel.verified ? 'bg-green-900/50 text-green-400' : 'bg-gray-900/50 text-gray-400'
                  }`}>
                    {intel.verified ? (
                      <>
                        <CheckCircle className="w-3 h-3" />
                        <span>Verified</span>
                      </>
                    ) : (
                      <>
                        <Clock className="w-3 h-3" />
                        <span>Unverified</span>
                      </>
                    )}
                  </span>
                </div>
              </div>

              {/* Actions */}
              <div className="flex items-center space-x-2 ml-4">
                <button
                  onClick={() => handleVerifyIntel(intel)}
                  className={`p-2 rounded-md transition-colors ${
                    intel.verified 
                      ? 'text-yellow-400 hover:bg-yellow-900/50' 
                      : 'text-green-400 hover:bg-green-900/50'
                  }`}
                  title={intel.verified ? 'Mark as unverified' : 'Mark as verified'}
                >
                  {intel.verified ? <X className="w-4 h-4" /> : <CheckCircle className="w-4 h-4" />}
                </button>
                <button
                  onClick={() => handleEditIntel(intel)}
                  className="p-2 text-gray-400 hover:text-blue-400 hover:bg-blue-900/50 rounded-md transition-colors"
                  title="Edit report"
                >
                  <Edit className="w-4 h-4" />
                </button>
                <button
                  onClick={() => handleDeleteIntel(intel.id)}
                  className="p-2 text-gray-400 hover:text-red-400 hover:bg-red-900/50 rounded-md transition-colors"
                  title="Delete report"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* No Results */}
      {filteredIntelligence.length === 0 && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="tactical-card p-12 text-center"
        >
          <Eye className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-300 mb-2">No Intelligence Reports Found</h3>
          <p className="text-gray-400 mb-4">
            {searchTerm || typeFilter !== 'ALL' || severityFilter !== 'ALL' || verifiedFilter !== 'ALL'
              ? 'Try adjusting your search criteria or filters.'
              : 'No intelligence reports have been added yet.'}
          </p>
          <button
            onClick={() => setShowAddModal(true)}
            className="tactical-button-primary"
          >
            Add First Report
          </button>
        </motion.div>
      )}

      {/* Add Intelligence Modal */}
      <Modal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        title="Add Intelligence Report"
        size="lg"
      >
        <IntelligenceForm
          onSubmit={(intelData) => {
            actions.addIntelligence(intelData);
            setShowAddModal(false);
          }}
          onCancel={() => setShowAddModal(false)}
        />
      </Modal>

      {/* Edit Intelligence Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => {
          setShowEditModal(false);
          setSelectedIntel(null);
        }}
        title="Edit Intelligence Report"
        size="lg"
      >
        {selectedIntel && (
          <IntelligenceForm
            intelligence={selectedIntel}
            onSubmit={(intelData) => {
              actions.updateIntelligence({ ...intelData, id: selectedIntel.id });
              setShowEditModal(false);
              setSelectedIntel(null);
            }}
            onCancel={() => {
              setShowEditModal(false);
              setSelectedIntel(null);
            }}
          />
        )}
      </Modal>
    </div>
  );
}

export default Intelligence;
