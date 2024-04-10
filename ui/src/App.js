import React from "react";
import VertexButton from "./components/VertexButton";
import Vertex from "./components/Vertex";

function App() {
  let [buttons, setVertex] = React.useState([]);
  let [count, updateCount] = React.useState(1);



  function createVertex() {
    const v = count + 1
    updateCount(v)
    setVertex([...buttons,
      {id: count, 
       data: <Vertex key={count} id={count.toString()}  data={count}/>,
       x: 0,
       y: 0}]
    );
  }


  function updateButtonCoordinates (id, x, y){    
    setVertex(buttons.map(button => {
      return button.id === id ? { ...button, x, y } : button;}
      ))
  }

  return (
    <div> 
      <div className='title'>
        <p>GraphResearcher!!!</p>
      </div>
      <button className='button' onClick={() => createVertex()}> click on me</button>
      <VertexButton buttonsInput={buttons} func={updateButtonCoordinates}/>
    </div>
  );
}

export default App;

