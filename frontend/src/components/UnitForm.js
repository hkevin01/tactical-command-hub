import { motion } from 'framer-motion';
import { useState } from 'react';

function UnitForm({ unit, onSubmit, onCancel }) {
  const [formData, setFormData] = useState({
    callsign: unit?.callsign || '',
    unitName: unit?.unitName || '',
    unitType: unit?.unitType || 'Infantry',
    domain: unit?.domain || 'LAND',
    status: unit?.status || 'OPERATIONAL',
    currentLatitude: unit?.currentLatitude || 0,
    currentLongitude: unit?.currentLongitude || 0,
    altitude: unit?.altitude || 0,
    heading: unit?.heading || 0,
    speed: unit?.speed || 0,
    maxPersonnel: unit?.maxPersonnel || 1,
    currentPersonnel: unit?.currentPersonnel || 1,
    equipmentStatus: unit?.equipmentStatus || 'FULLY_OPERATIONAL',
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.callsign.trim()) {
      newErrors.callsign = 'Callsign is required';
    }

    if (!formData.unitName.trim()) {
      newErrors.unitName = 'Unit name is required';
    }

    if (formData.currentLatitude < -90 || formData.currentLatitude > 90) {
      newErrors.currentLatitude = 'Latitude must be between -90 and 90';
    }

    if (formData.currentLongitude < -180 || formData.currentLongitude > 180) {
      newErrors.currentLongitude = 'Longitude must be between -180 and 180';
    }

    if (formData.currentPersonnel > formData.maxPersonnel) {
      newErrors.currentPersonnel = 'Current personnel cannot exceed max personnel';
    }

    if (formData.maxPersonnel < 1) {
      newErrors.maxPersonnel = 'Max personnel must be at least 1';
    }

    if (formData.heading < 0 || formData.heading >= 360) {
      newErrors.heading = 'Heading must be between 0 and 359 degrees';
    }

    if (formData.speed < 0) {
      newErrors.speed = 'Speed cannot be negative';
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

    onSubmit({
      ...formData,
      currentLatitude: parseFloat(formData.currentLatitude),
      currentLongitude: parseFloat(formData.currentLongitude),
      altitude: parseFloat(formData.altitude),
      heading: parseFloat(formData.heading),
      speed: parseFloat(formData.speed),
      maxPersonnel: parseInt(formData.maxPersonnel),
      currentPersonnel: parseInt(formData.currentPersonnel),
      lastReportTime: new Date().toISOString(),
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

  const inputClass = (field) => 
    `tactical-input ${errors[field] ? 'border-red-500 focus:border-red-500 focus:ring-red-500' : ''}`;

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Basic Information */}
      <div>
        <h3 className="text-lg font-medium text-gray-100 mb-4">Basic Information</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Callsign *
            </label>
            <input
              type="text"
              value={formData.callsign}
              onChange={(e) => handleChange('callsign', e.target.value)}
              placeholder="e.g., ALPHA-6"
              className={inputClass('callsign')}
            />
            {errors.callsign && (
              <p className="mt-1 text-sm text-red-400">{errors.callsign}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Unit Name *
            </label>
            <input
              type="text"
              value={formData.unitName}
              onChange={(e) => handleChange('unitName', e.target.value)}
              placeholder="e.g., Alpha Company"
              className={inputClass('unitName')}
            />
            {errors.unitName && (
              <p className="mt-1 text-sm text-red-400">{errors.unitName}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Unit Type
            </label>
            <select
              value={formData.unitType}
              onChange={(e) => handleChange('unitType', e.target.value)}
              className={inputClass('unitType')}
            >
              <option value="Infantry">Infantry</option>
              <option value="Armor">Armor</option>
              <option value="Aviation">Aviation</option>
              <option value="Artillery">Artillery</option>
              <option value="Engineering">Engineering</option>
              <option value="Medical">Medical</option>
              <option value="Logistics">Logistics</option>
              <option value="Special Forces">Special Forces</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Domain
            </label>
            <select
              value={formData.domain}
              onChange={(e) => handleChange('domain', e.target.value)}
              className={inputClass('domain')}
            >
              <option value="LAND">Land</option>
              <option value="AIR">Air</option>
              <option value="SEA">Sea</option>
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
              <option value="OPERATIONAL">Operational</option>
              <option value="MAINTENANCE">Maintenance</option>
              <option value="OFFLINE">Offline</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Equipment Status
            </label>
            <select
              value={formData.equipmentStatus}
              onChange={(e) => handleChange('equipmentStatus', e.target.value)}
              className={inputClass('equipmentStatus')}
            >
              <option value="FULLY_OPERATIONAL">Fully Operational</option>
              <option value="PARTIAL">Partial</option>
              <option value="MAINTENANCE_REQUIRED">Maintenance Required</option>
              <option value="NON_OPERATIONAL">Non-Operational</option>
            </select>
          </div>
        </div>
      </div>

      {/* Position & Movement */}
      <div>
        <h3 className="text-lg font-medium text-gray-100 mb-4">Position & Movement</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Latitude
            </label>
            <input
              type="number"
              step="any"
              value={formData.currentLatitude}
              onChange={(e) => handleChange('currentLatitude', e.target.value)}
              placeholder="e.g., 34.0522"
              className={inputClass('currentLatitude')}
            />
            {errors.currentLatitude && (
              <p className="mt-1 text-sm text-red-400">{errors.currentLatitude}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Longitude
            </label>
            <input
              type="number"
              step="any"
              value={formData.currentLongitude}
              onChange={(e) => handleChange('currentLongitude', e.target.value)}
              placeholder="e.g., -118.2437"
              className={inputClass('currentLongitude')}
            />
            {errors.currentLongitude && (
              <p className="mt-1 text-sm text-red-400">{errors.currentLongitude}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Altitude (m)
            </label>
            <input
              type="number"
              step="any"
              value={formData.altitude}
              onChange={(e) => handleChange('altitude', e.target.value)}
              placeholder="e.g., 100"
              className={inputClass('altitude')}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Heading (degrees)
            </label>
            <input
              type="number"
              min="0"
              max="359"
              value={formData.heading}
              onChange={(e) => handleChange('heading', e.target.value)}
              placeholder="e.g., 90"
              className={inputClass('heading')}
            />
            {errors.heading && (
              <p className="mt-1 text-sm text-red-400">{errors.heading}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Speed (km/h)
            </label>
            <input
              type="number"
              min="0"
              step="any"
              value={formData.speed}
              onChange={(e) => handleChange('speed', e.target.value)}
              placeholder="e.g., 5.5"
              className={inputClass('speed')}
            />
            {errors.speed && (
              <p className="mt-1 text-sm text-red-400">{errors.speed}</p>
            )}
          </div>
        </div>
      </div>

      {/* Personnel */}
      <div>
        <h3 className="text-lg font-medium text-gray-100 mb-4">Personnel</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Max Personnel
            </label>
            <input
              type="number"
              min="1"
              value={formData.maxPersonnel}
              onChange={(e) => handleChange('maxPersonnel', e.target.value)}
              className={inputClass('maxPersonnel')}
            />
            {errors.maxPersonnel && (
              <p className="mt-1 text-sm text-red-400">{errors.maxPersonnel}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Current Personnel
            </label>
            <input
              type="number"
              min="0"
              max={formData.maxPersonnel}
              value={formData.currentPersonnel}
              onChange={(e) => handleChange('currentPersonnel', e.target.value)}
              className={inputClass('currentPersonnel')}
            />
            {errors.currentPersonnel && (
              <p className="mt-1 text-sm text-red-400">{errors.currentPersonnel}</p>
            )}
          </div>
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
            unit ? 'Update Unit' : 'Create Unit'
          )}
        </motion.button>
      </div>
    </form>
  );
}

export default UnitForm;
