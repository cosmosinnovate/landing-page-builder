import React from 'react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { ContentSection, ContentBlock } from '@/types';
import BlockComponent from './BlockComponent';
import { GripVertical, Trash2, Settings } from 'lucide-react';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { useDroppable } from '@dnd-kit/core';

interface SectionComponentProps {
  section: ContentSection;
  isSelected: boolean;
  onSelect: () => void;
  onUpdate: (updates: Partial<ContentSection>) => void;
  onDelete: () => void;
  onUpdateBlock: (blockId: string, updates: Partial<ContentBlock>) => void;
  onDeleteBlock: (blockId: string) => void;
  selectedBlockId: string | null;
  onSelectBlock: (blockId: string | null) => void;
}

const SectionComponent: React.FC<SectionComponentProps> = ({
  section,
  isSelected,
  onSelect,
  onUpdate,
  onDelete,
  onUpdateBlock,
  onDeleteBlock,
  selectedBlockId,
  onSelectBlock,
}) => {
  const {
    attributes,
    listeners,
    setNodeRef: setSortableRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: section.id });

  const { setNodeRef: setDroppableRef } = useDroppable({
    id: section.id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <div
      ref={setSortableRef}
      style={style}
      className={`mb-6 bg-white rounded-lg border-2 transition-all ${
        isSelected
          ? 'border-primary-500 shadow-lg'
          : isDragging
          ? 'border-gray-300 opacity-50'
          : 'border-gray-200 hover:border-gray-300'
      }`}
      onClick={(e) => {
        e.stopPropagation();
        onSelect();
      }}
    >
      {/* Section Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-gray-50 border-b rounded-t-lg">
        <div className="flex items-center space-x-3">
          <button
            {...attributes}
            {...listeners}
            className="cursor-move text-gray-400 hover:text-gray-600"
          >
            <GripVertical className="w-5 h-5" />
          </button>
          <span className="text-sm font-semibold text-gray-900">{section.type}</span>
          <span className="text-xs text-gray-500">
            {section.blocks.length} block{section.blocks.length !== 1 ? 's' : ''}
          </span>
        </div>
        <div className="flex items-center space-x-2">
          <button
            onClick={(e) => {
              e.stopPropagation();
              // Open settings modal
            }}
            className="p-1 text-gray-400 hover:text-gray-600"
          >
            <Settings className="w-4 h-4" />
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation();
              if (confirm('Delete this section?')) {
                onDelete();
              }
            }}
            className="p-1 text-red-400 hover:text-red-600"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Section Content */}
      <div ref={setDroppableRef} className="p-6 min-h-[100px]">
        {section.blocks.length === 0 ? (
          <div className="text-center py-8 border-2 border-dashed border-gray-300 rounded-lg">
            <p className="text-sm text-gray-500">
              Drag and drop blocks here or click to add content
            </p>
          </div>
        ) : (
          <SortableContext
            items={section.blocks.map((b) => b.id)}
            strategy={verticalListSortingStrategy}
          >
            <div className="space-y-4">
              {section.blocks.map((block) => (
                <BlockComponent
                  key={block.id}
                  block={block}
                  isSelected={selectedBlockId === block.id}
                  onSelect={() => onSelectBlock(block.id)}
                  onUpdate={(updates) => onUpdateBlock(block.id, updates)}
                  onDelete={() => onDeleteBlock(block.id)}
                />
              ))}
            </div>
          </SortableContext>
        )}
      </div>
    </div>
  );
};

export default SectionComponent;

