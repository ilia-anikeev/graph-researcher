import React, { useLayoutEffect } from "react";
import VertexButton from "./components/VertexButton";
import Vertex from "./components/Vertex";
import GraphMetadata from "./components/GraphResearchInfo"

function App() {
  const [vertices, setVertex] = React.useState([]);
  const [count, updateCount] = React.useState(1);
  const [edges,setEdges]=React.useState([]);
  const [edgeCounter,setEdgeCounter]=React.useState(1);


  function createEdge(ids,idt){
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
            ctx.lineWidth=4;
            const v1=vertices.find(element => element.id===ids);
            const v2=vertices.find(element => element.id===idt);
            ctx.beginPath();
            ctx.moveTo(v1.x,v1.y);
            ctx.lineTo(v2.x,v2.y);
            ctx.stroke();
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
  //const handleMouseDown = event => {};
  //const handleMouseUp = event => {};
  return (
    <div> 
      <div >
        <p className='title'>Graph Researcher</p>
      </div>
      <div className="menu">
        <div style={{paddingTop: "3rem"}}><button className='button' onClick={() => createVertex()}> Create vertex</button>
        <VertexButton vertices={vertices} updateButtonCoordinates={updateButtonCoordinates} createEdge={createEdge}/>
        </div>
        <div style={{paddingTop: "3rem"}}><GraphMetadata/></div>
        <div>
          <canvas
                id="canvas"
                width={window.innerWidth}
                height={window.innerHeight}
                color='grey'
               // onMouseDown={handleMouseDown}
               // onMouseUp={handleMouseUp}
               >
                Canvas
             </canvas>
          </div>
      </div>
    </div>
  );
}

export default App;

