import React, { useEffect, useState } from 'react';
import { Button } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import PropTypes from 'prop-types';
import '../index.css';
import './GraphArchive.css';


function GraphArchive(props) {
    const [isOpen, setIsOpen] = useState(false);

    const openGraphArchive = () => {
        setIsOpen(true);
        props.setIsGraphArchiveMode(true);
    }


    useEffect (() => {
        if (!isOpen){
            props.setIsGraphArchiveMode(false);
        }
    })


    const displayGraph = (graph) => {
        var vertices = graph['graph']['vertices'];
        var edges = graph['graph']['edges'];

        const radius = 300;
        const centerX = 800;
        const centerY = 400;
        const angle = Math.PI * 2 / vertices.length;
        for (var i = 0; i < vertices.length; ++i){
            vertices[i].x = centerX + radius * Math.cos(angle * i);
            vertices[i].y = centerY + radius * Math.sin(angle * i);
            ++vertices[i].index;
            vertices[i].data = vertices[i].index.toString();
        }
        for (var j = 0; j < edges.length; ++j){
            edges[j] = {source: edges[j].source.index + 1,
                        target: edges[j].target.index + 1,
                        id: j + 1,
                        weight: edges[j].weight,
                        data: edges[j].data
                        }
        }

        props.addVertex(vertices);
        props.addEdge(edges);
        props.updateVertexCount(vertices.length + 1);
        props.setEdgeCounter(edges.length + 1);
        props.setIsDirected(graph['graph']['info']['isDirected']);
        props.setIsWeighted(graph['graph']['info']['isWeighted']);
        props.setHasSelfLoops(graph['graph']['info']['hasSelfLoops'] | 0);
        props.sethasMultipleEdges(graph['graph']['info']['hasMultipleEdges'] | 0);
        props.setGraphName(graph['graph']['info']['graphName']);

        setIsOpen(false);
    }


    const getGraph = (name) => {
        fetch('http://localhost:8080/get_famous_graph?graph_name=' + name, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Connection': 'keep-alive'
            }
        })
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
            <Button className='button' onClick={openGraphArchive} type='text' block> Graph Archive</Button>
            {isOpen && <div className='GraphArchive'>
                            <div className='GraphArchive-body'>
                                <Button style={{marginBottom: '1.5rem', float: 'right'}} onClick={() => setIsOpen(false)} icon={<CloseOutlined/>}></Button>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <Button className='GraphButton' onClick={getPetersenGraph}>
                                        Petersen Graph
                                    </Button>
                                </div>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <Button className='GraphButton' onClick={getHerschelGraph}>
                                        Herschel Graph
                                    </Button>
                                </div>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <Button className='GraphButton' onClick={getApollonianNetwork}>
                                        Apollonian Network
                                    </Button>
                                </div>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <Button className='GraphButton' onClick={getChvatalGraph}>
                                        Chvátal Graph
                                    </Button>
                                </div>
                                <div style={{paddingTop: '2.7rem'}}>
                                    <Button className='GraphButton' onClick={getGrotzschGraph}>
                                        Grötzsch Graph
                                    </Button>
                                </div>
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
    setEdgeCounter: PropTypes.func,
    setIsDirected: PropTypes.func,
    setIsWeighted: PropTypes.func,
    setHasSelfLoops: PropTypes.func,
    sethasMultipleEdges: PropTypes.func,
    setIsGraphArchiveMode: PropTypes.func,
    setGraphName: PropTypes.func
}