import React, { useState } from 'react';
import PropTypes from 'prop-types';
import '../index.css';
import './GraphArchive.css';


function GraphArchive(props) {
    const [isOpen, setIsOpen] = useState(false);



    const openGraphArchive = () => {
        setIsOpen(true);
    }

    const displayGraph = (graph) => {
        var vertices = graph['vertices'];
        var edges = graph['edges'];

        const radius = 300;
        const centerX = 800;
        const centerY = 400;
        const angle = Math.PI * 2 / vertices.length;
        for (var i = 0; i < vertices.length; ++i){
            vertices[i].x = centerX + radius * Math.cos(angle * i);
            vertices[i].y = centerY + radius * Math.sin(angle * i); 
        }
        for (var j = 0; j < edges.length; ++j){
            edges[j] = {source: edges[j].source.index,
                        target: edges[j].target.index,
                        weight: edges[j].weight,
                        data: edges[j].data
                        } 
        }
        props.addVertex(vertices);
        props.addEdge(edges);
        props.updateVertexCount(vertices.length + 1);
        document.querySelectorAll('.vertex').forEach(element => element.remove());
        setIsOpen(false);
    }

    const getGraph = (name) => {
        fetch('http://localhost:8080/get_famous_graph?graph_name=' + name)
        .then(response => response.json())
        .then(graph => displayGraph(graph))
        .catch(error => console.log(error));
    }

    const getPetersenGraph = () => {
        getGraph('Petersen');
    }

    const getHerschelGraph = () => {
        getGraph('Herschel');
    }

    const getApollonianNetwork = () => {
        getGraph('Apollonian');
    }

    const getChvatalGraph = () => {
        getGraph('Chvatal');
    }

    const getGrotzschGraph = () => {
        getGraph('Grotzsch');
    }


    return (
        <div>
            <button className='button' onClick={openGraphArchive}> Graph Archive</button>
            {isOpen && <div className='GraphArchive'>
                            <div className='GraphArchive-body'>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <button className='GraphButton' onClick={getPetersenGraph}> 
                                        Petersen graph
                                    </button>
                                </div>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <button className='GraphButton' onClick={getHerschelGraph}>  
                                        Herschel graph
                                    </button>
                                </div>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <button className='GraphButton' onClick={getApollonianNetwork}> 
                                        Apollonian network
                                    </button>
                                </div>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <button className='GraphButton' onClick={getChvatalGraph}> 
                                        Chvátal graph
                                    </button>
                                </div>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <button className='GraphButton' onClick={getGrotzschGraph}> 
                                        Grötzsch Graph
                                    </button>
                                </div>
                                <button style={{alignSelf: 'right', marginTop: '2.7rem'}} onClick={() => setIsOpen(false)}>Close</button>
                            </div> 
                        </div>}
        </div>
    )
}

export default GraphArchive;

GraphArchive.propTypes = {
    addVertex: PropTypes.func,
    addEdge: PropTypes.func,
    updateVertexCount: PropTypes.func,
}