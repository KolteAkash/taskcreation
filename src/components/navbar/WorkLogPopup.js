
import React, { useState } from 'react';
import './WorkLogPopup.css';

const WorkLogPopup = ({ onSave, onCancel, setWorkLogData }) => {
  const [logTime, setLogTime] = useState('');
  const [dateStarted, setDateStarted] = useState('');
  const [workDescription, setWorkDescription] = useState('');

  const handleSave = () => {
    if (!logTime.trim()) {
      console.error('Invalid Log Time:', logTime);
      return;
    }

    onSave(logTime, dateStarted, workDescription);
    setLogTime('');
    setDateStarted('');
    setWorkDescription('');
  };

  const handleCancel = () => {
    onCancel();
  };

  return (
    <div className="work-log-popup-container">
      <div className="abc">
        <h2>Time Tracking</h2>
      </div>
      <div className="input-group">
        <div className="input-wrapper">
          <label>Log Time</label>
          <input
            type="text"
            value={logTime}
            onChange={(e) => setLogTime(e.target.value)}
          />
        </div>
        <div className="input-wrapper">
          <label>Date Started</label>
          <input
            type="datetime-local"
            value={dateStarted}
            onChange={(e) => setDateStarted(e.target.value)}
          />
        </div>
      </div>
      <div className="descp">
        <label>Work Description</label>
        <input
          type="text"
          value={workDescription}
          onChange={(e) => setWorkDescription(e.target.value)}
        />
      </div>
      <div className="button-container">
        <div className="save-button">
          <button onClick={handleSave}>Save</button>
        </div>
        <div className="cancel-button">
          <button onClick={handleCancel}>Cancel</button>
        </div>
      </div>
    </div>
  );
};

export default WorkLogPopup;