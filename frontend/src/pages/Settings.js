import { motion } from 'framer-motion';
import {
    AlertTriangle,
    Bell,
    CheckCircle,
    Database,
    Download,
    Monitor,
    RefreshCw,
    Save,
    Shield,
    Upload
} from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';

function Settings() {
  const [activeTab, setActiveTab] = useState('general');
  const [settings, setSettings] = useState({
    general: {
      systemName: 'Tactical Command Hub',
      timeZone: 'UTC',
      language: 'English',
      autoRefresh: true,
      refreshInterval: 5,
    },
    display: {
      theme: 'dark',
      animations: true,
      compactMode: false,
      showTooltips: true,
      highContrast: false,
    },
    notifications: {
      enableNotifications: true,
      soundAlerts: true,
      emailNotifications: false,
      highPriorityOnly: false,
      notificationTimeout: 5,
    },
    security: {
      sessionTimeout: 30,
      requireMFA: false,
      passwordComplexity: 'high',
      loginAttempts: 3,
      encryptData: true,
    },
    backup: {
      autoBackup: true,
      backupInterval: 24,
      retentionDays: 30,
      includeIntelligence: true,
      includeMissions: true,
      includeUnits: true,
    }
  });

  const [isLoading, setIsLoading] = useState(false);

  const tabs = [
    { id: 'general', label: 'General', icon: Monitor },
    { id: 'display', label: 'Display', icon: Monitor },
    { id: 'notifications', label: 'Notifications', icon: Bell },
    { id: 'security', label: 'Security', icon: Shield },
    { id: 'backup', label: 'Backup', icon: Database },
  ];

  const handleSettingChange = (category, setting, value) => {
    setSettings(prev => ({
      ...prev,
      [category]: {
        ...prev[category],
        [setting]: value
      }
    }));
  };

  const handleSaveSettings = async () => {
    setIsLoading(true);
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    toast.success('Settings saved successfully');
    setIsLoading(false);
  };

  const handleResetSettings = () => {
    if (window.confirm('Are you sure you want to reset all settings to default values?')) {
      // Reset to default values
      setSettings({
        general: {
          systemName: 'Tactical Command Hub',
          timeZone: 'UTC',
          language: 'English',
          autoRefresh: true,
          refreshInterval: 5,
        },
        display: {
          theme: 'dark',
          animations: true,
          compactMode: false,
          showTooltips: true,
          highContrast: false,
        },
        notifications: {
          enableNotifications: true,
          soundAlerts: true,
          emailNotifications: false,
          highPriorityOnly: false,
          notificationTimeout: 5,
        },
        security: {
          sessionTimeout: 30,
          requireMFA: false,
          passwordComplexity: 'high',
          loginAttempts: 3,
          encryptData: true,
        },
        backup: {
          autoBackup: true,
          backupInterval: 24,
          retentionDays: 30,
          includeIntelligence: true,
          includeMissions: true,
          includeUnits: true,
        }
      });
      toast.success('Settings reset to defaults');
    }
  };

  const handleExportSettings = () => {
    const dataStr = JSON.stringify(settings, null, 2);
    const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
    
    const exportFileDefaultName = `tactical_hub_settings_${new Date().toISOString().slice(0,10)}.json`;
    
    const linkElement = document.createElement('a');
    linkElement.setAttribute('href', dataUri);
    linkElement.setAttribute('download', exportFileDefaultName);
    linkElement.click();
    
    toast.success('Settings exported successfully');
  };

  const handleImportSettings = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const importedSettings = JSON.parse(e.target.result);
          setSettings(importedSettings);
          toast.success('Settings imported successfully');
        } catch (error) {
          toast.error('Failed to import settings: Invalid file format');
        }
      };
      reader.readAsText(file);
    }
  };

  const renderGeneralSettings = () => (
    <div className="space-y-6">
      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          System Name
        </label>
        <input
          type="text"
          value={settings.general.systemName}
          onChange={(e) => handleSettingChange('general', 'systemName', e.target.value)}
          className="tactical-input w-full max-w-md"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Time Zone
        </label>
        <select
          value={settings.general.timeZone}
          onChange={(e) => handleSettingChange('general', 'timeZone', e.target.value)}
          className="tactical-input w-full max-w-md"
        >
          <option value="UTC">UTC</option>
          <option value="EST">Eastern Standard Time</option>
          <option value="PST">Pacific Standard Time</option>
          <option value="GMT">Greenwich Mean Time</option>
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Language
        </label>
        <select
          value={settings.general.language}
          onChange={(e) => handleSettingChange('general', 'language', e.target.value)}
          className="tactical-input w-full max-w-md"
        >
          <option value="English">English</option>
          <option value="Spanish">Spanish</option>
          <option value="French">French</option>
          <option value="German">German</option>
        </select>
      </div>

      <div className="flex items-center space-x-3">
        <input
          type="checkbox"
          id="autoRefresh"
          checked={settings.general.autoRefresh}
          onChange={(e) => handleSettingChange('general', 'autoRefresh', e.target.checked)}
          className="w-4 h-4 text-primary-600 bg-military-800 border-military-600 rounded focus:ring-primary-500"
        />
        <label htmlFor="autoRefresh" className="text-sm font-medium text-gray-300">
          Enable auto-refresh
        </label>
      </div>

      {settings.general.autoRefresh && (
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-2">
            Refresh Interval (seconds)
          </label>
          <input
            type="number"
            min="1"
            max="60"
            value={settings.general.refreshInterval}
            onChange={(e) => handleSettingChange('general', 'refreshInterval', parseInt(e.target.value))}
            className="tactical-input w-32"
          />
        </div>
      )}
    </div>
  );

  const renderDisplaySettings = () => (
    <div className="space-y-6">
      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Theme
        </label>
        <select
          value={settings.display.theme}
          onChange={(e) => handleSettingChange('display', 'theme', e.target.value)}
          className="tactical-input w-full max-w-md"
        >
          <option value="dark">Dark</option>
          <option value="light">Light</option>
          <option value="auto">Auto</option>
        </select>
      </div>

      <div className="space-y-4">
        {[
          { key: 'animations', label: 'Enable animations', description: 'Use smooth transitions and effects' },
          { key: 'compactMode', label: 'Compact mode', description: 'Reduce spacing and padding for more content' },
          { key: 'showTooltips', label: 'Show tooltips', description: 'Display helpful tips on hover' },
          { key: 'highContrast', label: 'High contrast', description: 'Increase contrast for better visibility' },
        ].map(({ key, label, description }) => (
          <div key={key} className="flex items-start space-x-3">
            <input
              type="checkbox"
              id={key}
              checked={settings.display[key]}
              onChange={(e) => handleSettingChange('display', key, e.target.checked)}
              className="w-4 h-4 text-primary-600 bg-military-800 border-military-600 rounded focus:ring-primary-500 mt-1"
            />
            <div>
              <label htmlFor={key} className="text-sm font-medium text-gray-300">
                {label}
              </label>
              <p className="text-xs text-gray-400">{description}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );

  const renderNotificationSettings = () => (
    <div className="space-y-6">
      <div className="space-y-4">
        {[
          { key: 'enableNotifications', label: 'Enable notifications', description: 'Show system notifications' },
          { key: 'soundAlerts', label: 'Sound alerts', description: 'Play sounds for important notifications' },
          { key: 'emailNotifications', label: 'Email notifications', description: 'Send notifications via email' },
          { key: 'highPriorityOnly', label: 'High priority only', description: 'Only show high priority alerts' },
        ].map(({ key, label, description }) => (
          <div key={key} className="flex items-start space-x-3">
            <input
              type="checkbox"
              id={key}
              checked={settings.notifications[key]}
              onChange={(e) => handleSettingChange('notifications', key, e.target.checked)}
              className="w-4 h-4 text-primary-600 bg-military-800 border-military-600 rounded focus:ring-primary-500 mt-1"
            />
            <div>
              <label htmlFor={key} className="text-sm font-medium text-gray-300">
                {label}
              </label>
              <p className="text-xs text-gray-400">{description}</p>
            </div>
          </div>
        ))}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Notification Timeout (seconds)
        </label>
        <input
          type="number"
          min="1"
          max="30"
          value={settings.notifications.notificationTimeout}
          onChange={(e) => handleSettingChange('notifications', 'notificationTimeout', parseInt(e.target.value))}
          className="tactical-input w-32"
        />
      </div>
    </div>
  );

  const renderSecuritySettings = () => (
    <div className="space-y-6">
      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Session Timeout (minutes)
        </label>
        <input
          type="number"
          min="5"
          max="480"
          value={settings.security.sessionTimeout}
          onChange={(e) => handleSettingChange('security', 'sessionTimeout', parseInt(e.target.value))}
          className="tactical-input w-32"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Password Complexity
        </label>
        <select
          value={settings.security.passwordComplexity}
          onChange={(e) => handleSettingChange('security', 'passwordComplexity', e.target.value)}
          className="tactical-input w-full max-w-md"
        >
          <option value="low">Low</option>
          <option value="medium">Medium</option>
          <option value="high">High</option>
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Max Login Attempts
        </label>
        <input
          type="number"
          min="1"
          max="10"
          value={settings.security.loginAttempts}
          onChange={(e) => handleSettingChange('security', 'loginAttempts', parseInt(e.target.value))}
          className="tactical-input w-32"
        />
      </div>

      <div className="space-y-4">
        {[
          { key: 'requireMFA', label: 'Require Multi-Factor Authentication', description: 'Add extra security layer' },
          { key: 'encryptData', label: 'Encrypt stored data', description: 'Encrypt sensitive data at rest' },
        ].map(({ key, label, description }) => (
          <div key={key} className="flex items-start space-x-3">
            <input
              type="checkbox"
              id={key}
              checked={settings.security[key]}
              onChange={(e) => handleSettingChange('security', key, e.target.checked)}
              className="w-4 h-4 text-primary-600 bg-military-800 border-military-600 rounded focus:ring-primary-500 mt-1"
            />
            <div>
              <label htmlFor={key} className="text-sm font-medium text-gray-300">
                {label}
              </label>
              <p className="text-xs text-gray-400">{description}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );

  const renderBackupSettings = () => (
    <div className="space-y-6">
      <div className="flex items-start space-x-3">
        <input
          type="checkbox"
          id="autoBackup"
          checked={settings.backup.autoBackup}
          onChange={(e) => handleSettingChange('backup', 'autoBackup', e.target.checked)}
          className="w-4 h-4 text-primary-600 bg-military-800 border-military-600 rounded focus:ring-primary-500 mt-1"
        />
        <div>
          <label htmlFor="autoBackup" className="text-sm font-medium text-gray-300">
            Enable automatic backups
          </label>
          <p className="text-xs text-gray-400">Automatically backup system data</p>
        </div>
      </div>

      {settings.backup.autoBackup && (
        <>
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Backup Interval (hours)
            </label>
            <input
              type="number"
              min="1"
              max="168"
              value={settings.backup.backupInterval}
              onChange={(e) => handleSettingChange('backup', 'backupInterval', parseInt(e.target.value))}
              className="tactical-input w-32"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Retention Period (days)
            </label>
            <input
              type="number"
              min="1"
              max="365"
              value={settings.backup.retentionDays}
              onChange={(e) => handleSettingChange('backup', 'retentionDays', parseInt(e.target.value))}
              className="tactical-input w-32"
            />
          </div>

          <div className="space-y-4">
            <h4 className="text-sm font-medium text-gray-300">Include in backups:</h4>
            {[
              { key: 'includeIntelligence', label: 'Intelligence reports' },
              { key: 'includeMissions', label: 'Mission data' },
              { key: 'includeUnits', label: 'Unit information' },
            ].map(({ key, label }) => (
              <div key={key} className="flex items-center space-x-3">
                <input
                  type="checkbox"
                  id={key}
                  checked={settings.backup[key]}
                  onChange={(e) => handleSettingChange('backup', key, e.target.checked)}
                  className="w-4 h-4 text-primary-600 bg-military-800 border-military-600 rounded focus:ring-primary-500"
                />
                <label htmlFor={key} className="text-sm text-gray-300">
                  {label}
                </label>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );

  const renderTabContent = () => {
    switch (activeTab) {
      case 'general': return renderGeneralSettings();
      case 'display': return renderDisplaySettings();
      case 'notifications': return renderNotificationSettings();
      case 'security': return renderSecuritySettings();
      case 'backup': return renderBackupSettings();
      default: return renderGeneralSettings();
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
          <h1 className="text-3xl font-bold text-gray-100">System Settings</h1>
          <p className="text-gray-400 mt-1">Configure system preferences and security</p>
        </div>
        <div className="flex items-center space-x-3 mt-4 sm:mt-0">
          <label className="tactical-button-secondary flex items-center space-x-2 cursor-pointer">
            <Upload className="w-4 h-4" />
            <span>Import</span>
            <input
              type="file"
              accept=".json"
              onChange={handleImportSettings}
              className="sr-only"
            />
          </label>
          <button
            onClick={handleExportSettings}
            className="tactical-button-secondary flex items-center space-x-2"
          >
            <Download className="w-4 h-4" />
            <span>Export</span>
          </button>
        </div>
      </motion.div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Tabs */}
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-1"
        >
          <div className="tactical-card p-4">
            <nav className="space-y-2">
              {tabs.map((tab) => {
                const Icon = tab.icon;
                return (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`w-full flex items-center space-x-3 px-3 py-2 rounded-md text-left transition-colors ${
                      activeTab === tab.id
                        ? 'bg-primary-600 text-white'
                        : 'text-gray-300 hover:bg-military-700'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span className="text-sm font-medium">{tab.label}</span>
                  </button>
                );
              })}
            </nav>
          </div>
        </motion.div>

        {/* Content */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-3"
        >
          <div className="tactical-card p-6">
            <div className="mb-6">
              <h2 className="text-xl font-bold text-gray-100 mb-2">
                {tabs.find(tab => tab.id === activeTab)?.label} Settings
              </h2>
              <hr className="border-military-700" />
            </div>

            {renderTabContent()}

            {/* Action Buttons */}
            <div className="flex items-center justify-between pt-6 mt-6 border-t border-military-700">
              <button
                onClick={handleResetSettings}
                className="tactical-button-secondary flex items-center space-x-2"
              >
                <RefreshCw className="w-4 h-4" />
                <span>Reset to Defaults</span>
              </button>

              <motion.button
                onClick={handleSaveSettings}
                disabled={isLoading}
                className="tactical-button-primary flex items-center space-x-2"
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                {isLoading ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    <span>Saving...</span>
                  </>
                ) : (
                  <>
                    <Save className="w-4 h-4" />
                    <span>Save Settings</span>
                  </>
                )}
              </motion.button>
            </div>
          </div>
        </motion.div>
      </div>

      {/* System Status */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="tactical-card p-6"
      >
        <h3 className="text-lg font-medium text-gray-100 mb-4">System Status</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="flex items-center space-x-3">
            <CheckCircle className="w-5 h-5 text-green-400" />
            <div>
              <p className="text-sm font-medium text-gray-300">Database</p>
              <p className="text-xs text-green-400">Connected</p>
            </div>
          </div>
          <div className="flex items-center space-x-3">
            <CheckCircle className="w-5 h-5 text-green-400" />
            <div>
              <p className="text-sm font-medium text-gray-300">API Services</p>
              <p className="text-xs text-green-400">Operational</p>
            </div>
          </div>
          <div className="flex items-center space-x-3">
            <AlertTriangle className="w-5 h-5 text-yellow-400" />
            <div>
              <p className="text-sm font-medium text-gray-300">Backup System</p>
              <p className="text-xs text-yellow-400">Warning</p>
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
}

export default Settings;
