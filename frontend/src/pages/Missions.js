import { motion } from 'framer-motion';
import {
    Calendar,
    CheckCircle,
    Clock,
    Pause,
    Play,
    Plus,
    Search,
    Square,
    Target,
    Users
} from 'lucide-react';
import { useState } from 'react';
import MissionForm from '../components/MissionForm';
import Modal from '../components/Modal';
import { useTactical } from '../context/TacticalContext';

function Missions() {
  const { state, actions } = useTactical();
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedMission, setSelectedMission] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [priorityFilter, setPriorityFilter] = useState('ALL');

  const filteredMissions = state.missions.filter(mission => {
    const matchesSearch = mission.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         mission.type.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || mission.status === statusFilter;
    const matchesPriority = priorityFilter === 'ALL' || mission.priority === priorityFilter;
    
    return matchesSearch && matchesStatus && matchesPriority;
  });

  const handleEditMission = (mission) => {
    setSelectedMission(mission);
    setShowEditModal(true);
  };

  const handleDeleteMission = (missionId) => {
    if (window.confirm('Are you sure you want to delete this mission?')) {
      actions.deleteMission(missionId);
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'ACTIVE': return <Play className="w-4 h-4" />;
      case 'PAUSED': return <Pause className="w-4 h-4" />;
      case 'COMPLETED': return <CheckCircle className="w-4 h-4" />;
      case 'CANCELLED': return <Square className="w-4 h-4" />;
      default: return <Clock className="w-4 h-4" />;
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'text-green-400 bg-green-900/50';
      case 'PAUSED': return 'text-yellow-400 bg-yellow-900/50';
      case 'COMPLETED': return 'text-blue-400 bg-blue-900/50';
      case 'CANCELLED': return 'text-red-400 bg-red-900/50';
      default: return 'text-gray-400 bg-gray-900/50';
    }
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'HIGH': return 'text-red-400 bg-red-900/50';
      case 'MEDIUM': return 'text-yellow-400 bg-yellow-900/50';
      case 'LOW': return 'text-green-400 bg-green-900/50';
      default: return 'text-gray-400 bg-gray-900/50';
    }
  };

  const getAssignedUnitsNames = (unitIds) => {
    return unitIds.map(id => {
      const unit = state.units.find(u => u.id === id);
      return unit ? unit.callsign : `Unit ${id}`;
    }).join(', ');
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
          <h1 className="text-3xl font-bold text-gray-100">Mission Control</h1>
          <p className="text-gray-400 mt-1">Plan, execute, and monitor tactical missions</p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="tactical-button-primary flex items-center space-x-2 mt-4 sm:mt-0"
        >
          <Plus className="w-4 h-4" />
          <span>Create Mission</span>
        </button>
      </motion.div>

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
              placeholder="Search missions..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="tactical-input pl-10 pr-4 py-2 w-full"
            />
          </div>
          
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="tactical-input"
          >
            <option value="ALL">All Statuses</option>
            <option value="ACTIVE">Active</option>
            <option value="PAUSED">Paused</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
          
          <select
            value={priorityFilter}
            onChange={(e) => setPriorityFilter(e.target.value)}
            className="tactical-input"
          >
            <option value="ALL">All Priorities</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>
        </div>
      </motion.div>

      {/* Missions List */}
      <div className="space-y-4">
        {filteredMissions.map((mission, index) => (
          <motion.div
            key={mission.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="tactical-card p-6 hover:bg-military-700 transition-all duration-300"
          >
            <div className="flex flex-col lg:flex-row lg:items-center justify-between">
              {/* Mission Info */}
              <div className="flex-1">
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <div className="flex items-center space-x-3 mb-2">
                      <h3 className="text-xl font-bold text-gray-100">{mission.name}</h3>
                      <span className={`px-2 py-1 rounded-full text-xs font-medium flex items-center space-x-1 ${getStatusColor(mission.status)}`}>
                        {getStatusIcon(mission.status)}
                        <span>{mission.status}</span>
                      </span>
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${getPriorityColor(mission.priority)}`}>
                        {mission.priority} PRIORITY
                      </span>
                    </div>
                    <p className="text-gray-400 mb-3">{mission.description}</p>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
                  <div className="flex items-center space-x-2 text-sm">
                    <Target className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-300">Type: {mission.type}</span>
                  </div>
                  
                  <div className="flex items-center space-x-2 text-sm">
                    <Users className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-300">
                      Units: {getAssignedUnitsNames(mission.assignedUnits)}
                    </span>
                  </div>
                  
                  <div className="flex items-center space-x-2 text-sm">
                    <Calendar className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-300">
                      Start: {new Date(mission.startDate).toLocaleDateString()}
                    </span>
                  </div>
                  
                  <div className="flex items-center space-x-2 text-sm">
                    <Clock className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-300">
                      End: {new Date(mission.endDate).toLocaleDateString()}
                    </span>
                  </div>
                </div>

                {/* Progress Bar */}
                <div className="mb-4">
                  <div className="flex justify-between text-sm text-gray-400 mb-2">
                    <span>Mission Progress</span>
                    <span>{mission.progress.toFixed(0)}%</span>
                  </div>
                  <div className="w-full bg-military-700 rounded-full h-3">
                    <div 
                      className={`h-3 rounded-full transition-all duration-500 ${
                        mission.progress >= 80 ? 'bg-green-400' :
                        mission.progress >= 50 ? 'bg-yellow-400' : 'bg-red-400'
                      }`}
                      style={{ width: `${mission.progress}%` }}
                    ></div>
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="flex items-center space-x-3 lg:ml-6">
                <button
                  onClick={() => handleEditMission(mission)}
                  className="tactical-button-secondary"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleDeleteMission(mission.id)}
                  className="tactical-button-danger"
                >
                  Delete
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* No Results */}
      {filteredMissions.length === 0 && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="tactical-card p-12 text-center"
        >
          <Target className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-300 mb-2">No Missions Found</h3>
          <p className="text-gray-400 mb-4">
            {searchTerm || statusFilter !== 'ALL' || priorityFilter !== 'ALL'
              ? 'Try adjusting your search criteria or filters.'
              : 'No missions have been created yet.'}
          </p>
          <button
            onClick={() => setShowAddModal(true)}
            className="tactical-button-primary"
          >
            Create First Mission
          </button>
        </motion.div>
      )}

      {/* Add Mission Modal */}
      <Modal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        title="Create New Mission"
        size="lg"
      >
        <MissionForm
          availableUnits={state.units}
          onSubmit={(missionData) => {
            actions.addMission(missionData);
            setShowAddModal(false);
          }}
          onCancel={() => setShowAddModal(false)}
        />
      </Modal>

      {/* Edit Mission Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => {
          setShowEditModal(false);
          setSelectedMission(null);
        }}
        title="Edit Mission"
        size="lg"
      >
        {selectedMission && (
          <MissionForm
            mission={selectedMission}
            availableUnits={state.units}
            onSubmit={(missionData) => {
              actions.updateMission({ ...missionData, id: selectedMission.id });
              setShowEditModal(false);
              setSelectedMission(null);
            }}
            onCancel={() => {
              setShowEditModal(false);
              setSelectedMission(null);
            }}
          />
        )}
      </Modal>
    </div>
  );
}

export default Missions;
