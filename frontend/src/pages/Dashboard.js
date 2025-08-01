import {
    ArcElement,
    BarElement,
    CategoryScale,
    Chart as ChartJS,
    Legend,
    LinearScale,
    LineElement,
    PointElement,
    Title,
    Tooltip,
} from 'chart.js';
import { motion } from 'framer-motion';
import {
    Activity,
    AlertTriangle,
    Clock,
    Radio,
    Shield,
    Target,
    TrendingUp,
    Users,
    Zap
} from 'lucide-react';
import { useEffect, useState } from 'react';
import { Bar, Doughnut, Line } from 'react-chartjs-2';
import { useTactical } from '../context/TacticalContext';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  BarElement
);

function Dashboard() {
  const { state } = useTactical();
  const [realTimeData, setRealTimeData] = useState({
    activeOperations: 0,
    systemLoad: 0,
    networkTraffic: 0,
  });

  // Simulate real-time data updates
  useEffect(() => {
    const interval = setInterval(() => {
      setRealTimeData({
        activeOperations: Math.floor(Math.random() * 15) + 5,
        systemLoad: Math.floor(Math.random() * 30) + 40,
        networkTraffic: Math.floor(Math.random() * 100) + 200,
      });
    }, 2000);

    return () => clearInterval(interval);
  }, []);

  // Calculate statistics
  const operationalUnits = state.units.filter(unit => unit.status === 'OPERATIONAL').length;
  const activeMissions = state.missions.filter(mission => mission.status === 'ACTIVE').length;
  const highPriorityIntel = state.intelligence.filter(intel => intel.severity === 'HIGH').length;
  const totalPersonnel = state.units.reduce((total, unit) => total + unit.currentPersonnel, 0);

  // Chart configurations
  const activityChartData = {
    labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'],
    datasets: [
      {
        label: 'Operations',
        data: [12, 19, 15, 25, 22, 18],
        borderColor: '#10b981',
        backgroundColor: 'rgba(16, 185, 129, 0.1)',
        tension: 0.4,
      },
      {
        label: 'Intelligence',
        data: [8, 12, 18, 15, 20, 14],
        borderColor: '#3b82f6',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        tension: 0.4,
      },
    ],
  };

  const unitStatusData = {
    labels: ['Operational', 'Maintenance', 'Offline'],
    datasets: [
      {
        data: [
          state.units.filter(u => u.status === 'OPERATIONAL').length,
          state.units.filter(u => u.status === 'MAINTENANCE').length,
          state.units.filter(u => u.status === 'OFFLINE').length,
        ],
        backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
        borderWidth: 0,
      },
    ],
  };

  const missionProgressData = {
    labels: state.missions.map(m => m.name.substring(0, 15) + '...'),
    datasets: [
      {
        label: 'Progress %',
        data: state.missions.map(m => m.progress),
        backgroundColor: state.missions.map(m => {
          if (m.progress >= 80) return '#10b981';
          if (m.progress >= 50) return '#f59e0b';
          return '#ef4444';
        }),
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        labels: {
          color: '#f1f5f9',
        },
      },
    },
    scales: {
      x: {
        ticks: { color: '#94a3b8' },
        grid: { color: '#475569' },
      },
      y: {
        ticks: { color: '#94a3b8' },
        grid: { color: '#475569' },
      },
    },
  };

  const StatCard = ({ title, value, icon: Icon, color, subtitle, trend }) => (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="tactical-card p-6 hover:bg-military-700 transition-all duration-300"
    >
      <div className="flex items-center justify-between">
        <div>
          <p className="text-gray-400 text-sm">{title}</p>
          <p className="text-2xl font-bold text-gray-100 mt-1">{value}</p>
          {subtitle && <p className="text-xs text-gray-500 mt-1">{subtitle}</p>}
          {trend && (
            <div className="flex items-center mt-2">
              <TrendingUp className={`w-3 h-3 mr-1 ${trend > 0 ? 'text-green-400' : 'text-red-400'}`} />
              <span className={`text-xs ${trend > 0 ? 'text-green-400' : 'text-red-400'}`}>
                {trend > 0 ? '+' : ''}{trend}%
              </span>
            </div>
          )}
        </div>
        <div className={`p-3 rounded-lg bg-${color}-500/20`}>
          <Icon className={`w-6 h-6 text-${color}-400`} />
        </div>
      </div>
    </motion.div>
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col sm:flex-row justify-between items-start sm:items-center"
      >
        <div>
          <h1 className="text-3xl font-bold text-gray-100">Command Dashboard</h1>
          <p className="text-gray-400 mt-1">Real-time tactical operations overview</p>
        </div>
        <div className="flex items-center space-x-2 mt-4 sm:mt-0">
          <div className="flex items-center space-x-2">
            <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
            <span className="text-sm text-gray-300">Live Data</span>
          </div>
        </div>
      </motion.div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Operational Units"
          value={operationalUnits}
          icon={Users}
          color="green"
          subtitle={`${state.units.length} total units`}
          trend={5}
        />
        <StatCard
          title="Active Missions"
          value={activeMissions}
          icon={Target}
          color="blue"
          subtitle={`${state.missions.length} total missions`}
          trend={-2}
        />
        <StatCard
          title="High Priority Intel"
          value={highPriorityIntel}
          icon={AlertTriangle}
          color="red"
          subtitle={`${state.intelligence.length} total reports`}
          trend={12}
        />
        <StatCard
          title="Personnel"
          value={totalPersonnel}
          icon={Shield}
          color="purple"
          subtitle="Active personnel"
          trend={3}
        />
      </div>

      {/* Real-time Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="tactical-card p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-100">Active Operations</h3>
            <Activity className="w-5 h-5 text-green-400" />
          </div>
          <div className="text-3xl font-bold text-green-400 mb-2">
            {realTimeData.activeOperations}
          </div>
          <div className="w-full bg-military-700 rounded-full h-2">
            <div 
              className="bg-green-400 h-2 rounded-full transition-all duration-500"
              style={{ width: `${(realTimeData.activeOperations / 20) * 100}%` }}
            ></div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.1 }}
          className="tactical-card p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-100">System Load</h3>
            <Zap className="w-5 h-5 text-yellow-400" />
          </div>
          <div className="text-3xl font-bold text-yellow-400 mb-2">
            {realTimeData.systemLoad}%
          </div>
          <div className="w-full bg-military-700 rounded-full h-2">
            <div 
              className="bg-yellow-400 h-2 rounded-full transition-all duration-500"
              style={{ width: `${realTimeData.systemLoad}%` }}
            ></div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.2 }}
          className="tactical-card p-6"
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-100">Network Traffic</h3>
            <Radio className="w-5 h-5 text-blue-400" />
          </div>
          <div className="text-3xl font-bold text-blue-400 mb-2">
            {realTimeData.networkTraffic} KB/s
          </div>
          <div className="w-full bg-military-700 rounded-full h-2">
            <div 
              className="bg-blue-400 h-2 rounded-full transition-all duration-500"
              style={{ width: `${(realTimeData.networkTraffic / 300) * 100}%` }}
            ></div>
          </div>
        </motion.div>
      </div>

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="tactical-card p-6"
        >
          <h3 className="text-lg font-medium text-gray-100 mb-4">Activity Timeline</h3>
          <Line data={activityChartData} options={chartOptions} />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="tactical-card p-6"
        >
          <h3 className="text-lg font-medium text-gray-100 mb-4">Unit Status Distribution</h3>
          <div className="h-64 flex items-center justify-center">
            <Doughnut 
              data={unitStatusData} 
              options={{
                ...chartOptions,
                maintainAspectRatio: false,
                plugins: {
                  ...chartOptions.plugins,
                  legend: {
                    position: 'bottom',
                    labels: {
                      color: '#f1f5f9',
                      padding: 20,
                    },
                  },
                },
              }} 
            />
          </div>
        </motion.div>
      </div>

      {/* Mission Progress Chart */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="tactical-card p-6"
      >
        <h3 className="text-lg font-medium text-gray-100 mb-4">Mission Progress Overview</h3>
        <Bar data={missionProgressData} options={chartOptions} />
      </motion.div>

      {/* Recent Activity */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="tactical-card p-6"
      >
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-medium text-gray-100">Recent Activity</h3>
          <Clock className="w-5 h-5 text-gray-400" />
        </div>
        <div className="space-y-3">
          {state.intelligence.slice(0, 5).map((intel, index) => (
            <div key={intel.id} className="flex items-center space-x-3 p-3 hover:bg-military-700 rounded-md transition-colors">
              <div className={`w-2 h-2 rounded-full ${
                intel.severity === 'HIGH' ? 'bg-red-400' :
                intel.severity === 'MEDIUM' ? 'bg-yellow-400' : 'bg-green-400'
              }`}></div>
              <div className="flex-1">
                <p className="text-sm text-gray-300">{intel.title}</p>
                <p className="text-xs text-gray-500">{new Date(intel.timestamp).toLocaleTimeString()}</p>
              </div>
              <span className={`text-xs px-2 py-1 rounded-full ${
                intel.type === 'THREAT' ? 'bg-red-900 text-red-300' :
                intel.type === 'INFO' ? 'bg-blue-900 text-blue-300' :
                'bg-yellow-900 text-yellow-300'
              }`}>
                {intel.type}
              </span>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  );
}

export default Dashboard;
