import React from "react";
import VertexButton from "./components/VertexButton";

function App() {
  const [buttons, setVertex] = React.useState([]);

    let x = 1;
    function createVertex() {
      setVertex(buttons.concat(
        [{id:x}]
      ));
      console.log(0);
      x = x + 1;
  }

  return (
    <div> 
      <div className='title'>
        <p>GraphResearcher!!!</p>
      </div>
      <button className='button' onClick={() => createVertex()}> click on me</button>
      <VertexButton buttonsInput={buttons}/>
    </div>
  );
}

export default App;
