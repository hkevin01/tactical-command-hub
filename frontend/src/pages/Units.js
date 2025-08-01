import { motion } from 'framer-motion';
import {
    Clock,
    Download,
    Edit,
    Filter,
    MapPin,
    Navigation,
    Plus,
    Search,
    Shield,
    Trash2,
    Users
} from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
import Modal from '../components/Modal';
import UnitForm from '../components/UnitForm';
import { useTactical } from '../context/TacticalContext';

function Units() {
  const { state, actions } = useTactical();
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedUnit, setSelectedUnit] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [domainFilter, setDomainFilter] = useState('ALL');
  const [showFilters, setShowFilters] = useState(false);

  // Filter units based on search and filters
  const filteredUnits = state.units.filter(unit => {
    const matchesSearch = unit.unitName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         unit.callsign.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || unit.status === statusFilter;
    const matchesDomain = domainFilter === 'ALL' || unit.domain === domainFilter;
    
    return matchesSearch && matchesStatus && matchesDomain;
  });

  const handleEditUnit = (unit) => {
    setSelectedUnit(unit);
    setShowEditModal(true);
  };

  const handleDeleteUnit = (unitId) => {
    if (window.confirm('Are you sure you want to delete this unit?')) {
      actions.deleteUnit(unitId);
    }
  };

  const handleExportData = () => {
    const dataStr = JSON.stringify(filteredUnits, null, 2);
    const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
    
    const exportFileDefaultName = `units_export_${new Date().toISOString().slice(0,10)}.json`;
    
    const linkElement = document.createElement('a');
    linkElement.setAttribute('href', dataUri);
    linkElement.setAttribute('download', exportFileDefaultName);
    linkElement.click();
    
    toast.success('Units data exported successfully');
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'OPERATIONAL': return 'text-green-400 bg-green-900/50';
      case 'MAINTENANCE': return 'text-yellow-400 bg-yellow-900/50';
      case 'OFFLINE': return 'text-red-400 bg-red-900/50';
      default: return 'text-gray-400 bg-gray-900/50';
    }
  };

  const getDomainIcon = (domain) => {
    switch (domain) {
      case 'LAND': return '🚛';
      case 'AIR': return '✈️';
      case 'SEA': return '🚢';
      default: return '📍';
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
          <h1 className="text-3xl font-bold text-gray-100">Unit Management</h1>
          <p className="text-gray-400 mt-1">Manage and monitor tactical units</p>
        </div>
        <div className="flex items-center space-x-3 mt-4 sm:mt-0">
          <button
            onClick={handleExportData}
            className="tactical-button-secondary flex items-center space-x-2"
          >
            <Download className="w-4 h-4" />
            <span>Export</span>
          </button>
          <button
            onClick={() => setShowAddModal(true)}
            className="tactical-button-primary flex items-center space-x-2"
          >
            <Plus className="w-4 h-4" />
            <span>Add Unit</span>
          </button>
        </div>
      </motion.div>

      {/* Search and Filters */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="tactical-card p-6"
      >
        <div className="flex flex-col lg:flex-row gap-4">
          {/* Search */}
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search units by name or callsign..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="tactical-input pl-10 pr-4 py-2 w-full"
            />
          </div>

          {/* Filter Toggle */}
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="tactical-button-secondary flex items-center space-x-2"
          >
            <Filter className="w-4 h-4" />
            <span>Filters</span>
          </button>
        </div>

        {/* Expandable Filters */}
        {showFilters && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            className="mt-4 pt-4 border-t border-military-700"
          >
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Status</label>
                <select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                  className="tactical-input w-full"
                >
                  <option value="ALL">All Statuses</option>
                  <option value="OPERATIONAL">Operational</option>
                  <option value="MAINTENANCE">Maintenance</option>
                  <option value="OFFLINE">Offline</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Domain</label>
                <select
                  value={domainFilter}
                  onChange={(e) => setDomainFilter(e.target.value)}
                  className="tactical-input w-full"
                >
                  <option value="ALL">All Domains</option>
                  <option value="LAND">Land</option>
                  <option value="AIR">Air</option>
                  <option value="SEA">Sea</option>
                </select>
              </div>
            </div>
          </motion.div>
        )}
      </motion.div>

      {/* Units Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
        {filteredUnits.map((unit, index) => (
          <motion.div
            key={unit.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="tactical-card p-6 hover:bg-military-700 transition-all duration-300"
          >
            {/* Unit Header */}
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-center space-x-3">
                <div className="text-2xl">{getDomainIcon(unit.domain)}</div>
                <div>
                  <h3 className="font-bold text-gray-100">{unit.callsign}</h3>
                  <p className="text-sm text-gray-400">{unit.unitName}</p>
                </div>
              </div>
              <div className="flex items-center space-x-2">
                <button
                  onClick={() => handleEditUnit(unit)}
                  className="p-2 text-gray-400 hover:text-blue-400 transition-colors"
                >
                  <Edit className="w-4 h-4" />
                </button>
                <button
                  onClick={() => handleDeleteUnit(unit.id)}
                  className="p-2 text-gray-400 hover:text-red-400 transition-colors"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Status Badge */}
            <div className="mb-4">
              <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(unit.status)}`}>
                {unit.status}
              </span>
            </div>

            {/* Unit Details */}
            <div className="space-y-3">
              <div className="flex items-center space-x-2 text-sm">
                <Users className="w-4 h-4 text-gray-400" />
                <span className="text-gray-300">
                  {unit.currentPersonnel}/{unit.maxPersonnel} Personnel
                </span>
              </div>
              
              <div className="flex items-center space-x-2 text-sm">
                <MapPin className="w-4 h-4 text-gray-400" />
                <span className="text-gray-300">
                  {unit.currentLatitude.toFixed(4)}, {unit.currentLongitude.toFixed(4)}
                </span>
              </div>
              
              <div className="flex items-center space-x-2 text-sm">
                <Navigation className="w-4 h-4 text-gray-400" />
                <span className="text-gray-300">
                  {unit.heading.toFixed(0)}° @ {unit.speed.toFixed(1)} km/h
                </span>
              </div>
              
              <div className="flex items-center space-x-2 text-sm">
                <Shield className="w-4 h-4 text-gray-400" />
                <span className="text-gray-300">
                  Equipment: {unit.equipmentStatus}
                </span>
              </div>
              
              <div className="flex items-center space-x-2 text-sm">
                <Clock className="w-4 h-4 text-gray-400" />
                <span className="text-gray-300">
                  Last Report: {new Date(unit.lastReportTime).toLocaleTimeString()}
                </span>
              </div>
            </div>

            {/* Progress Bar for Personnel */}
            <div className="mt-4">
              <div className="flex justify-between text-xs text-gray-400 mb-1">
                <span>Personnel Status</span>
                <span>{Math.round((unit.currentPersonnel / unit.maxPersonnel) * 100)}%</span>
              </div>
              <div className="w-full bg-military-700 rounded-full h-2">
                <div 
                  className="bg-blue-400 h-2 rounded-full transition-all duration-300"
                  style={{ width: `${(unit.currentPersonnel / unit.maxPersonnel) * 100}%` }}
                ></div>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* No Results */}
      {filteredUnits.length === 0 && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="tactical-card p-12 text-center"
        >
          <Users className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-300 mb-2">No Units Found</h3>
          <p className="text-gray-400 mb-4">
            {searchTerm || statusFilter !== 'ALL' || domainFilter !== 'ALL'
              ? 'Try adjusting your search criteria or filters.'
              : 'No units have been added yet.'}
          </p>
          <button
            onClick={() => setShowAddModal(true)}
            className="tactical-button-primary"
          >
            Add First Unit
          </button>
        </motion.div>
      )}

      {/* Add Unit Modal */}
      <Modal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        title="Add New Unit"
      >
        <UnitForm
          onSubmit={(unitData) => {
            actions.addUnit(unitData);
            setShowAddModal(false);
          }}
          onCancel={() => setShowAddModal(false)}
        />
      </Modal>

      {/* Edit Unit Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => {
          setShowEditModal(false);
          setSelectedUnit(null);
        }}
        title="Edit Unit"
      >
        {selectedUnit && (
          <UnitForm
            unit={selectedUnit}
            onSubmit={(unitData) => {
              actions.updateUnit({ ...unitData, id: selectedUnit.id });
              setShowEditModal(false);
              setSelectedUnit(null);
            }}
            onCancel={() => {
              setShowEditModal(false);
              setSelectedUnit(null);
            }}
          />
        )}
      </Modal>
    </div>
  );
}

export default Units;
