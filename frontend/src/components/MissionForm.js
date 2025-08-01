import { motion } from 'framer-motion';
import { useState } from 'react';

function MissionForm({ mission, availableUnits, onSubmit, onCancel }) {
  const [formData, setFormData] = useState({
    name: mission?.name || '',
    type: mission?.type || 'Reconnaissance',
    status: mission?.status || 'ACTIVE',
    priority: mission?.priority || 'MEDIUM',
    startDate: mission?.startDate ? mission.startDate.split('T')[0] : new Date().toISOString().split('T')[0],
    startTime: mission?.startDate ? mission.startDate.split('T')[1].split('.')[0] : '08:00',
    endDate: mission?.endDate ? mission.endDate.split('T')[0] : new Date(Date.now() + 86400000).toISOString().split('T')[0],
    endTime: mission?.endDate ? mission.endDate.split('T')[1].split('.')[0] : '18:00',
    assignedUnits: mission?.assignedUnits || [],
    progress: mission?.progress || 0,
    description: mission?.description || '',
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Mission name is required';
    }

    if (!formData.description.trim()) {
      newErrors.description = 'Mission description is required';
    }

    if (formData.assignedUnits.length === 0) {
      newErrors.assignedUnits = 'At least one unit must be assigned';
    }

    const startDateTime = new Date(`${formData.startDate}T${formData.startTime}`);
    const endDateTime = new Date(`${formData.endDate}T${formData.endTime}`);

    if (endDateTime <= startDateTime) {
      newErrors.endDate = 'End date/time must be after start date/time';
    }

    if (formData.progress < 0 || formData.progress > 100) {
      newErrors.progress = 'Progress must be between 0 and 100';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 1000));

    const startDate = new Date(`${formData.startDate}T${formData.startTime}`).toISOString();
    const endDate = new Date(`${formData.endDate}T${formData.endTime}`).toISOString();

    onSubmit({
      ...formData,
      startDate,
      endDate,
      progress: parseFloat(formData.progress),
    });

    setIsSubmitting(false);
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const handleUnitToggle = (unitId) => {
    const isSelected = formData.assignedUnits.includes(unitId);
    if (isSelected) {
      handleChange('assignedUnits', formData.assignedUnits.filter(id => id !== unitId));
    } else {
      handleChange('assignedUnits', [...formData.assignedUnits, unitId]);
    }
  };

  const inputClass = (field) => 
    `tactical-input ${errors[field] ? 'border-red-500 focus:border-red-500 focus:ring-red-500' : ''}`;

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Basic Information */}
      <div>
        <h3 className="text-lg font-medium text-gray-100 mb-4">Mission Details</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Mission Name *
            </label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => handleChange('name', e.target.value)}
              placeholder="e.g., Operation Thunder Strike"
              className={inputClass('name')}
            />
            {errors.name && (
              <p className="mt-1 text-sm text-red-400">{errors.name}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Mission Type
            </label>
            <select
              value={formData.type}
              onChange={(e) => handleChange('type', e.target.value)}
              className={inputClass('type')}
            >
              <option value="Reconnaissance">Reconnaissance</option>
              <option value="Security">Security</option>
              <option value="Logistics">Logistics</option>
              <option value="Combat">Combat</option>
              <option value="Training">Training</option>
              <option value="Medical">Medical</option>
              <option value="Engineering">Engineering</option>
              <option value="Intelligence">Intelligence</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Priority
            </label>
            <select
              value={formData.priority}
              onChange={(e) => handleChange('priority', e.target.value)}
              className={inputClass('priority')}
            >
              <option value="HIGH">High</option>
              <option value="MEDIUM">Medium</option>
              <option value="LOW">Low</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Status
            </label>
            <select
              value={formData.status}
              onChange={(e) => handleChange('status', e.target.value)}
              className={inputClass('status')}
            >
              <option value="ACTIVE">Active</option>
              <option value="PAUSED">Paused</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
              <option value="IN_PROGRESS">In Progress</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Progress (%)
            </label>
            <input
              type="number"
              min="0"
              max="100"
              value={formData.progress}
              onChange={(e) => handleChange('progress', e.target.value)}
              className={inputClass('progress')}
            />
            {errors.progress && (
              <p className="mt-1 text-sm text-red-400">{errors.progress}</p>
            )}
          </div>
        </div>
      </div>

      {/* Timeline */}
      <div>
        <h3 className="text-lg font-medium text-gray-100 mb-4">Timeline</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Start Date
            </label>
            <input
              type="date"
              value={formData.startDate}
              onChange={(e) => handleChange('startDate', e.target.value)}
              className={inputClass('startDate')}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Start Time
            </label>
            <input
              type="time"
              value={formData.startTime}
              onChange={(e) => handleChange('startTime', e.target.value)}
              className={inputClass('startTime')}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              End Date
            </label>
            <input
              type="date"
              value={formData.endDate}
              onChange={(e) => handleChange('endDate', e.target.value)}
              className={inputClass('endDate')}
            />
            {errors.endDate && (
              <p className="mt-1 text-sm text-red-400">{errors.endDate}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              End Time
            </label>
            <input
              type="time"
              value={formData.endTime}
              onChange={(e) => handleChange('endTime', e.target.value)}
              className={inputClass('endTime')}
            />
          </div>
        </div>
      </div>

      {/* Description */}
      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Mission Description *
        </label>
        <textarea
          value={formData.description}
          onChange={(e) => handleChange('description', e.target.value)}
          rows={3}
          placeholder="Describe the mission objectives and requirements..."
          className={inputClass('description')}
        />
        {errors.description && (
          <p className="mt-1 text-sm text-red-400">{errors.description}</p>
        )}
      </div>

      {/* Unit Assignment */}
      <div>
        <h3 className="text-lg font-medium text-gray-100 mb-4">Assigned Units *</h3>
        {errors.assignedUnits && (
          <p className="mb-3 text-sm text-red-400">{errors.assignedUnits}</p>
        )}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 max-h-48 overflow-y-auto">
          {availableUnits.map((unit) => (
            <label
              key={unit.id}
              className={`flex items-center p-3 border rounded-lg cursor-pointer transition-colors ${
                formData.assignedUnits.includes(unit.id)
                  ? 'border-primary-500 bg-primary-900/20'
                  : 'border-military-600 hover:border-military-500'
              }`}
            >
              <input
                type="checkbox"
                checked={formData.assignedUnits.includes(unit.id)}
                onChange={() => handleUnitToggle(unit.id)}
                className="sr-only"
              />
              <div className={`w-4 h-4 rounded border mr-3 flex items-center justify-center ${
                formData.assignedUnits.includes(unit.id)
                  ? 'bg-primary-600 border-primary-600'
                  : 'border-gray-400'
              }`}>
                {formData.assignedUnits.includes(unit.id) && (
                  <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                  </svg>
                )}
              </div>
              <div>
                <p className="font-medium text-gray-200">{unit.callsign}</p>
                <p className="text-xs text-gray-400">{unit.unitName} - {unit.status}</p>
              </div>
            </label>
          ))}
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex items-center justify-end space-x-3 pt-6 border-t border-military-700">
        <button
          type="button"
          onClick={onCancel}
          className="tactical-button-secondary"
          disabled={isSubmitting}
        >
          Cancel
        </button>
        <motion.button
          type="submit"
          className="tactical-button-primary"
          disabled={isSubmitting}
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
        >
          {isSubmitting ? (
            <div className="flex items-center space-x-2">
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              <span>Saving...</span>
            </div>
          ) : (
            mission ? 'Update Mission' : 'Create Mission'
          )}
        </motion.button>
      </div>
    </form>
  );
}

export default MissionForm;
