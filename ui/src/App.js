import React from "react";
import VertexButton from "./components/VertexButton";

function App() {
  const [vertices, setVertex] = React.useState([]);

    let x = 1;
    function createVertex() {
      setVertex(vertices.concat(
        [{id:x}]
      ));
      x = x + 1;
  }

  return (
    <div> 
      <div className='title'>
        <p>GraphResearcher!!!</p>
      </div>
      <button className='button' onClick={() => createVertex()}> click to create vertex</button>
      <VertexButton vertices={vertices}/>
    </div>
  );
}

export default App;
