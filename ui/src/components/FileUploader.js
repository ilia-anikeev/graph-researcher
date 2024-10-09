import { Button, Upload, message } from 'antd';
import React, { useContext} from 'react';
import { UserContext } from './UserContex';
import './FileUploader.css'; 

function FileUploader() {
    const { userID } = useContext(UserContext);


  const onChange = (info) => {


    if (info.file.status === 'done') {
      message.success(`${info.file.name} file uploaded successfully.`);
    } else if (info.file.status === 'error') {
      message.error(`${info.file.name} file upload failed.`);
    }
  };

  const customRequest = ({ file, onSuccess, onError }) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userID', userID);

    fetch('http://localhost:8080/upload', {
      method: 'POST',
      body: formData,
    })
    .then(response => {
      if (response.ok) {
        onSuccess("ok");
      } else {
        onError("error");
      }
    })
    .catch(onError);
  };

  return (
    <div className="file-uploader">
      <Upload
        customRequest={customRequest}
        onChange={onChange}
        accept=".txt"
        maxCount={1}
      >
        <Button className='button' type='text' block>Load graph</Button>
      </Upload>
    </div>
  );
}

export default FileUploader;