import { motion } from 'framer-motion';
import { useState } from 'react';

function IntelligenceForm({ intelligence, onSubmit, onCancel }) {
  const [formData, setFormData] = useState({
    title: intelligence?.title || '',
    type: intelligence?.type || 'INFO',
    severity: intelligence?.severity || 'MEDIUM',
    description: intelligence?.description || '',
    lat: intelligence?.location?.lat || 0,
    lng: intelligence?.location?.lng || 0,
    verified: intelligence?.verified || false,
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.title.trim()) {
      newErrors.title = 'Title is required';
    }

    if (!formData.description.trim()) {
      newErrors.description = 'Description is required';
    }

    if (formData.lat < -90 || formData.lat > 90) {
      newErrors.lat = 'Latitude must be between -90 and 90';
    }

    if (formData.lng < -180 || formData.lng > 180) {
      newErrors.lng = 'Longitude must be between -180 and 180';
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
      title: formData.title,
      type: formData.type,
      severity: formData.severity,
      description: formData.description,
      location: {
        lat: parseFloat(formData.lat),
        lng: parseFloat(formData.lng),
      },
      verified: formData.verified,
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
        <h3 className="text-lg font-medium text-gray-100 mb-4">Report Details</h3>
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Report Title *
            </label>
            <input
              type="text"
              value={formData.title}
              onChange={(e) => handleChange('title', e.target.value)}
              placeholder="e.g., Enemy Movement Detected"
              className={inputClass('title')}
            />
            {errors.title && (
              <p className="mt-1 text-sm text-red-400">{errors.title}</p>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Report Type
              </label>
              <select
                value={formData.type}
                onChange={(e) => handleChange('type', e.target.value)}
                className={inputClass('type')}
              >
                <option value="THREAT">Threat</option>
                <option value="INFO">Information</option>
                <option value="ENVIRONMENTAL">Environmental</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Severity Level
              </label>
              <select
                value={formData.severity}
                onChange={(e) => handleChange('severity', e.target.value)}
                className={inputClass('severity')}
              >
                <option value="HIGH">High</option>
                <option value="MEDIUM">Medium</option>
                <option value="LOW">Low</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Description *
            </label>
            <textarea
              value={formData.description}
              onChange={(e) => handleChange('description', e.target.value)}
              rows={4}
              placeholder="Provide detailed information about the intelligence..."
              className={inputClass('description')}
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-400">{errors.description}</p>
            )}
          </div>
        </div>
      </div>

      {/* Location */}
      <div>
        <h3 className="text-lg font-medium text-gray-100 mb-4">Location</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Latitude
            </label>
            <input
              type="number"
              step="any"
              value={formData.lat}
              onChange={(e) => handleChange('lat', e.target.value)}
              placeholder="e.g., 34.0522"
              className={inputClass('lat')}
            />
            {errors.lat && (
              <p className="mt-1 text-sm text-red-400">{errors.lat}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Longitude
            </label>
            <input
              type="number"
              step="any"
              value={formData.lng}
              onChange={(e) => handleChange('lng', e.target.value)}
              placeholder="e.g., -118.2437"
              className={inputClass('lng')}
            />
            {errors.lng && (
              <p className="mt-1 text-sm text-red-400">{errors.lng}</p>
            )}
          </div>
        </div>
      </div>

      {/* Verification Status */}
      <div>
        <label className="flex items-center space-x-3 cursor-pointer">
          <input
            type="checkbox"
            checked={formData.verified}
            onChange={(e) => handleChange('verified', e.target.checked)}
            className="w-4 h-4 text-primary-600 bg-military-800 border-military-600 rounded focus:ring-primary-500 focus:ring-2"
          />
          <span className="text-sm font-medium text-gray-300">
            Mark as verified intelligence
          </span>
        </label>
        <p className="mt-1 text-xs text-gray-400">
          Check this box if the intelligence has been confirmed through multiple sources
        </p>
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
            intelligence ? 'Update Report' : 'Create Report'
          )}
        </motion.button>
      </div>
    </form>
  );
}

export default IntelligenceForm;
