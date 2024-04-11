import React, { useLayoutEffect } from "react";
import VertexButton from "./components/VertexButton";
import Vertex from "./components/Vertex";

function App() {
  const [vertices, setVertex] = React.useState([]);
  const [count, updateCount] = React.useState(1);
  const [edges,setEdges]=React.useState([]);
  const [edgeCounter,setEdgeCounter]=React.useState(1);




  function createEdge(ids,idt){
    console.log("huy");
    const newCounter=edgeCounter+1
    setEdgeCounter(newCounter)

    setEdges([...edges,{source: ids,
    target: idt,
    id: edgeCounter
    }])
  }



  function createVertex() {
    const newCount = count + 1
    updateCount(newCount)
    setVertex([...vertices,
      {id: count, 
       vertex: <Vertex key={count} id={count}  data={count}/>,
       x: 0, y: 0}]
    )
  }
    const draw = (ids,idt)=>{
            const canvas = document.getElementById("canvas");
            const ctx = canvas.getContext("2d");
            console.log("ilyagay");
            ctx.lineWidth=10;
            const v1=vertices.find(element => element.id===ids);
            console.log(vertices);
            console.log(ids);

            console.log("gay");
            const v2=vertices.find(element => element.id===idt);
            ctx.beginPath();
            ctx.moveTo(v1.x,v1.y);
            ctx.lineTo(v2.x,v2.y);
            ctx.stroke();
            console.log("huy");

    };
  function updateButtonCoordinates (id, x, y){    
    setVertex(vertices.map(vertex => {
      return vertex.id === id ? { ...vertex, x, y } : vertex}
      ))
  }
  useLayoutEffect(() =>{
    const canvas=document.getElementById('canvas');
    const ctx = canvas.getContext("2d");
    ctx.clearRect(0,0,canvas.width, canvas.height);
    edges.forEach(element=>{
      draw(element.source,element.target)
    })
  });
 const handleMouseDown = event => {};
  const handleMouseUp = event => {};
   //const handleMouseMove = event => {};
  return (
    <div> 
      <div className='title'>
        <p>GraphResearcher!!!</p>
      </div>

      <button className='button' onClick={() => createVertex()}> click on me</button>
      <div>
      <VertexButton vertices={vertices} updateButtonCoordinates={updateButtonCoordinates} createEdge={createEdge}/>

      
        <canvas
                id="canvas"
                width={window.innerWidth-4}
                height={window.innerHeight-4}
                onMouseDown={handleMouseDown}
                onMouseUp={handleMouseUp}
              
              >
                Canvas
              </canvas>
              </div>

      {vertices.forEach(element => {
        console.log("id:", element.id, element.x, element.y)
        })}

    </div>
  );
}

export default App;

