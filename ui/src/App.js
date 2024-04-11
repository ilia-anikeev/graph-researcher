import React from "react";
import VertexButton from "./components/VertexButton";
import Vertex from "./components/Vertex";

function App() {
  const [vertices, setVertex] = React.useState([]);
  const [count, updateCount] = React.useState(1);

  function createVertex() {
    const newCount = count + 1
    updateCount(newCount)
    setVertex([...vertices,
      {id: count, 
       vertex: <Vertex key={count} id={count}  data={count}/>,
       x: 0, y: 0}]
    )
  }

  function updateButtonCoordinates (id, x, y){    
    setVertex(vertices.map(vertex => {
      return vertex.id === id ? { ...vertex, x, y } : vertex}
      ))
  }

  return (
    <div> 
      <div className='title'>
        <p>GraphResearcher!!!</p>
      </div>
      <button className='button' onClick={() => createVertex()}> click on me</button>
      <VertexButton vertices={vertices} updateButtonCoordinates={updateButtonCoordinates}/>
      {vertices.forEach(element => {
        console.log("id:", element.id, element.x, element.y)
        })}
    </div>
  );
}

export default App;

