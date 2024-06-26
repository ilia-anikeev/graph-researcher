import React, {useRef, useEffect, useState} from 'react';
import './Researcher.css';
import Researcher from './Researcher';
import Vertex from "./Vertex";
import Edge from './Edge';

function Graph() {
    const [vertices, addVertex] = useState([]);
    const [vertexCounter, updateVertexCount] = useState(1);
    const [edges, addEdge]= useState([]);
    const [edgeCounter, setEdgeCounter] = useState(1);
    const [isDirected, setIsDirected] = useState(false);
    const [isWeighted, setIsWeighted] = useState(false);
    const [hasSelfLoops, setHasSelfLoops] = useState(0);
    const [hasMultipleEdges, sethasMultipleEdges] = useState(0);

    const [removeEdgeMode, setEdgeRemoveMode] = useState(false);
    const [createEdgeMode, setEdgeCreateMode] = useState(false);
    const [vertexRemoveMode, setVertexRemoveMode] = useState(false);
    const [isGraphArchiveMode, setIsGraphArchiveMode] = useState(false);
    const [isUserGraphMode, setIsUserGraphMode] = useState(false);
    const [coordinates, updateCoordinates] = useState([]);
    const [drawEdgeMode, setDrawEdge] = useState(false);
    const [source, setSource] = useState(1);
    const [id, setId] = useState(1);
    const verticesRef = useRef([]);


    const createVertex = (xx, yy) => {
      setEdgeRemoveMode(false);
      setVertexRemoveMode(false);
      setEdgeCreateMode(false);
      const newVertexCounter = vertexCounter + 1;
      updateVertexCount(newVertexCounter);
      addVertex([...vertices,
        {index: vertexCounter,
         x: xx + 25, y: yy - 80,
         data: vertexCounter.toString()}]
      )
    }

    const updateVertexCoordinates = (index, newX, newY) => {
        addVertex(vertices.map(vertex => {
        return vertex.index === index ? { ...vertex, x: newX, y: newY } : vertex}
        )); 
    }

    const createEdge = (index) => {
        if (createEdgeMode === true) {
            if (drawEdgeMode === true){
                    if (index === source) {
                        const newhasSelfLoops = hasSelfLoops + 1;
                        setHasSelfLoops(newhasSelfLoops);
                    }
                    if (edges.find(edge => (edge.source === source & edge.target === index)
                                        || (edge.source === index & edge.target === source))) {
                        const newHasMultipleEdges = hasMultipleEdges + 1;
                        sethasMultipleEdges(newHasMultipleEdges);
                    }
                    setEdgeRemoveMode(false);
                    setVertexRemoveMode(false);
                    setEdgeCreateMode(false);
                    const newEdgeCounter = edgeCounter + 1;
                    setEdgeCounter(newEdgeCounter);
                    addEdge([...edges,
                            {source: source,
                            target: index,
                            id: edgeCounter,
                            data: '',
                            weight: 1}]
                    )
                    setDrawEdge(false);
            }else{
                setSource(index);
                setDrawEdge(true);
            }
        } 
    }

    const deleteVertex = (index) =>{
        if (vertexRemoveMode === true){
            addVertex(vertices.filter(vertex => (vertex.index !== index)));
            const loopsCount = edges.filter(edge => ((edge.source === index) & (edge.target === index))).length;
            console.log(loopsCount);
            setHasSelfLoops(hasSelfLoops - loopsCount);
            addEdge(edges.filter(edge => ((edge.source !== index) & (edge.target !== index))));
        }
        setVertexRemoveMode(false);
    }


    const deleteEdge = (index) => {
        if (removeEdgeMode === true){
            if (drawEdgeMode === true) {
                const edgeToDelete = edges.filter(edge => (edge.source === source & edge.target === index
                                                        || edge.source === index & edge.target === source));
                const indexToDelete = edges.indexOf(edgeToDelete[0]);
                if (indexToDelete >= 0) {
                    const newEdges = [...edges];
                    newEdges.splice(indexToDelete, 1);
                    addEdge(newEdges);
                    if (source === index) {
                        const newHasSelfLoops = hasSelfLoops - 1;
                        setHasSelfLoops(newHasSelfLoops);
                    }
                    if (edgeToDelete.length >= 2) {
                        const newHasMultipleEdges = hasMultipleEdges - 1;
                        setHasSelfLoops(newHasMultipleEdges);
                    }
                }
                setDrawEdge(false);
                setEdgeRemoveMode(false);
            }else{
                setSource(index);
                setDrawEdge(true);
            }
        }
    }

    const handleMouseMove = (index) => () => {
        setId(index);
        createEdge(index);
        deleteEdge(index);
        deleteVertex(index);
    };


    useEffect(() => {
      const setCoordinates = (event) => {
        updateCoordinates([event.clientX - 25, event.clientY - 25]);
      }

      const handleKeyPress = (event) => {
        if (event.key === 'v'){
          createVertex(coordinates[0], coordinates[1]);
        }
      };

      document.addEventListener('mousemove', setCoordinates);
      window.addEventListener('keypress', handleKeyPress);

      return (() => {
        document.removeEventListener('mousemove', setCoordinates);
        window.removeEventListener('keypress', handleKeyPress);
      })
    });


    useEffect(() => {                                                          //TODO
        const handleKeyPress = (event) => {
          if (event.key === 'd'){
            const verticesDOM = document.getElementsByClassName('vertex');
            const vertexDOM = Array.prototype.find.call(verticesDOM, (v) => (
                coordinates[1] <= v.getBoundingClientRect().bottom &&
                coordinates[1] >= v.getBoundingClientRect().top &&
                coordinates[0] <= v.getBoundingClientRect().right &&
                coordinates[0] >= v.getBoundingClientRect().left
            ));
            if (vertexDOM) {
                setVertexRemoveMode(true);
                deleteVertexx(parseInt(vertexDOM.children[0].value));
            }
          }
        };

        const deleteVertexx = (id) => {
            deleteVertex(id);
        }

        window.addEventListener('keypress', handleKeyPress);
    
        return (() => {
          window.removeEventListener('keypress', handleKeyPress);
        })
      });


    useEffect(() => {
        const mouseMove = (event) => {
            if (id !== 0) {
                const rect = event.target.getBoundingClientRect();
                const verticesDOM = document.getElementsByClassName('vertex');
                const vertexDOM = Array.prototype.find.call(verticesDOM, ver => (
                    rect.y <= ver.getBoundingClientRect().bottom &&
                    rect.y >= ver.getBoundingClientRect().top &&
                    rect.x <= ver.getBoundingClientRect().right &&
                    rect.x >= ver.getBoundingClientRect().left
                ));
                if (vertexDOM) {
                    updateVertexCoordinates(id,
                        vertexDOM.getBoundingClientRect().left + (vertexDOM.getBoundingClientRect().right - vertexDOM.getBoundingClientRect().left) / 2,
                        vertexDOM.getBoundingClientRect().top + (vertexDOM.getBoundingClientRect().top - vertexDOM.getBoundingClientRect().bottom) / 2 - 55
                    );
                }
            }
        };

        const mouseUp = () => {
            setId(0);
        };

        window.addEventListener('mousemove', mouseMove);
        window.addEventListener('mouseup', mouseUp);

        return () => {
            window.removeEventListener('mousemove', mouseMove);
            window.removeEventListener('mouseup', mouseUp);
        };
    })


        return (
            <div>
              <div>
                    {vertices ? vertices.map(vertex => {
                        return (
                            <div
                                key={'vertexContainer' + vertex.index.toString()}
                                ref={ref => verticesRef.current[vertex.index] = ref}
                                onMouseDown={handleMouseMove(vertex.index)}
                            >
                                <Vertex key={'vertex' + vertex.index.toString()}
                                        index={vertex.index}
                                        x={vertex.x}
                                        y={vertex.y}
                                        vertices={vertices}
                                        addVertex={addVertex}
                                        data={vertex.data}
                                        isGraphArchiveMode={isGraphArchiveMode}
                                        isUserGraphMode={isUserGraphMode}/>
                            </div>
                        );
                    }): null}
                </div>
                <div>
                {edges ? edges.map(edge => {
                    return(<div key={'edgeContainer' + edge.id.toString()}>
                        <Edge key={'edge' + edge.id.toString()}
                              isWeighted={isWeighted}
                              isDirected={isDirected}
                              vertices={vertices}
                              edges={edges}
                              addEdge={addEdge}
                              weight={edge.weight}
                              source={edge.source}
                              target={edge.target}/>
                    </div>
                )
                }) : null}
                </div>
                <div>
                    <Researcher key={'researcher'}
                                createVertex={createVertex}
                                setVertexRemoveMode={setVertexRemoveMode}
                                setEdgeRemoveMode={setEdgeRemoveMode}
                                setEdgeCreateMode={setEdgeCreateMode}
                                setIsDirected={setIsDirected}
                                setIsWeighted={setIsWeighted}
                                isWeighted={isWeighted}
                                isDirected={isDirected}
                                hasSelfLoops={hasSelfLoops}
                                hasMultipleEdges={hasMultipleEdges}
                                vertices={vertices}
                                edges={edges}
                                addVertex={addVertex}
                                addEdge={addEdge}
                                updateVertexCount={updateVertexCount}
                                setEdgeCounter={setEdgeCounter}
                                setHasSelfLoops={setHasSelfLoops}
                                sethasMultipleEdges={sethasMultipleEdges}
                                setIsGraphArchiveMode={setIsGraphArchiveMode}
                                setIsUserGraphMode={setIsUserGraphMode}/>
                </div>
            </div>
          );
        }

export default Graph;